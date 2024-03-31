package org.hz.session.integration.demo.config;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.EntryEvictedListener;
import com.hazelcast.map.listener.EntryExpiredListener;
import com.hazelcast.map.listener.EntryRemovedListener;
import com.hazelcast.query.Predicates;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.session.*;
import org.springframework.session.events.SessionCreatedEvent;
import org.springframework.session.events.SessionDeletedEvent;
import org.springframework.session.events.SessionExpiredEvent;
import org.springframework.session.hazelcast.Hazelcast4IndexedSessionRepository;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class HzCustomSessionRepository implements FindByIndexNameSessionRepository<HzCustomSessionRepository.OtusHazelcastSession>, EntryAddedListener<String, MapSession>, EntryEvictedListener<String, MapSession>, EntryRemovedListener<String, MapSession>, EntryExpiredListener<String, MapSession> {

    public HzCustomSessionRepository(HazelcastInstance hazelcastInstance) {
        this.flushMode = FlushMode.ON_SAVE;
        this.saveMode = SaveMode.ON_SET_ATTRIBUTE;
        Assert.notNull(hazelcastInstance, "HazelcastInstance must not be null");
        this.hazelcastInstance = hazelcastInstance;
    }

    public static final String DEFAULT_SESSION_MAP_NAME = "spring:session:sessions";
    public static final String PRINCIPAL_NAME_ATTRIBUTE = "principalName";
    private static final String SPRING_SECURITY_CONTEXT = "SPRING_SECURITY_CONTEXT";
    private static final Log logger = LogFactory.getLog(Hazelcast4IndexedSessionRepository.class);
    private final HazelcastInstance hazelcastInstance;
    private ApplicationEventPublisher eventPublisher = (event) -> {
    };
    private Integer defaultMaxInactiveInterval;
    private IndexResolver<Session> indexResolver = new DelegatingIndexResolver(new PrincipalNameIndexResolver());
    private String sessionMapName = "spring:session:sessions";
    private FlushMode flushMode;
    private SaveMode saveMode;
    private IMap<String, MapSession> sessions;
    private UUID sessionListenerId;


    @PostConstruct
    public void init() {
        this.sessions = this.hazelcastInstance.getMap(this.sessionMapName);
        this.sessionListenerId = this.sessions.addEntryListener(this, true);
    }

    @PreDestroy
    public void close() {
        this.sessions.removeEntryListener(this.sessionListenerId);
    }

    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        Assert.notNull(applicationEventPublisher, "ApplicationEventPublisher cannot be null");
        this.eventPublisher = applicationEventPublisher;
    }

    public void setDefaultMaxInactiveInterval(Integer defaultMaxInactiveInterval) {
        this.defaultMaxInactiveInterval = defaultMaxInactiveInterval;
    }

    public void setIndexResolver(IndexResolver<Session> indexResolver) {
        Assert.notNull(indexResolver, "indexResolver cannot be null");
        this.indexResolver = indexResolver;
    }

    public void setSessionMapName(String sessionMapName) {
        Assert.hasText(sessionMapName, "Map name must not be empty");
        this.sessionMapName = sessionMapName;
    }

    public void setFlushMode(FlushMode flushMode) {
        Assert.notNull(flushMode, "flushMode cannot be null");
        this.flushMode = flushMode;
    }

    public void setSaveMode(SaveMode saveMode) {
        Assert.notNull(saveMode, "saveMode must not be null");
        this.saveMode = saveMode;
    }

    public OtusHazelcastSession createSession() {
        MapSession cached = new MapSession();
        if (this.defaultMaxInactiveInterval != null) {
            cached.setMaxInactiveInterval(Duration.ofSeconds((long) this.defaultMaxInactiveInterval));
        }

        OtusHazelcastSession session = new OtusHazelcastSession(cached, true);
        session.flushImmediateIfNecessary();
        return session;
    }


    public void save(OtusHazelcastSession session) {
        if (session.isNew) {
            this.sessions.set(session.getId(), session.getDelegate(), session.getMaxInactiveInterval().getSeconds(), TimeUnit.SECONDS);
        } else if (session.sessionIdChanged) {
            this.sessions.delete(session.originalId);
            session.originalId = session.getId();
            this.sessions.set(session.getId(), session.getDelegate(), session.getMaxInactiveInterval().getSeconds(), TimeUnit.SECONDS);
        } else if (session.hasChanges()) {
            HzCustomSessionUpdateEntryProcessor entryProcessor = new HzCustomSessionUpdateEntryProcessor();
            if (session.lastAccessedTimeChanged) {
                entryProcessor.setLastAccessedTime(session.getLastAccessedTime());
            }

            if (session.maxInactiveIntervalChanged) {
                entryProcessor.setMaxInactiveInterval(session.getMaxInactiveInterval());
            }

            if (!session.delta.isEmpty()) {
                entryProcessor.setDelta(new HashMap(session.delta));
            }

            this.sessions.executeOnKey(session.getId(), entryProcessor);
        }

        session.clearChangeFlags();
    }

    public OtusHazelcastSession findById(String id) {
        MapSession saved = (MapSession) this.sessions.get(id);
        if (saved == null) {
            return null;
        } else if (saved.isExpired()) {
            this.deleteById(saved.getId());
            return null;
        } else {
            return new OtusHazelcastSession(saved, false);
        }
    }

    public void deleteById(String id) {
        this.sessions.remove(id);
    }

    public SecurityContext fetchSecurityContextByPrincipalName(String principal) {
        var principalSessions = findByIndexNameAndIndexValue(PRINCIPAL_NAME_ATTRIBUTE, principal);
        return principalSessions.values()
                .stream()
                .filter(otusHazelcastSession -> !otusHazelcastSession.isExpired())
                .map(session -> (SecurityContext) session.getAttribute(SPRING_SECURITY_CONTEXT))
                .findFirst().orElseThrow();
    }

    @Override
    public Map<String, OtusHazelcastSession> findByIndexNameAndIndexValue(String indexName, String indexValue) {
        return (!PRINCIPAL_NAME_INDEX_NAME.equals(indexName))
                ? Collections.emptyMap()
                : this.sessions.values(Predicates.equal(PRINCIPAL_NAME_ATTRIBUTE, indexValue))
                .stream()
                .collect(Collectors.toMap(
                        MapSession::getId,
                        (s) -> new OtusHazelcastSession(s, false)));
    }

    public void entryAdded(EntryEvent<String, MapSession> event) {
        MapSession session = event.getValue();
        if (session.getId().equals(session.getOriginalId())) {
            if (logger.isDebugEnabled()) {
                logger.debug("Session created with id: " + session.getId());
            }
            this.eventPublisher.publishEvent(new SessionCreatedEvent(this, session));
        }

    }

    @Override
    public void entryEvicted(EntryEvent<String, MapSession> event) {
        if (logger.isDebugEnabled()) {
            logger.debug("Session expired with id: " + event.getOldValue().getId());
        }
        this.eventPublisher.publishEvent(new SessionExpiredEvent(this, event.getOldValue()));
    }

    public void entryRemoved(EntryEvent<String, MapSession> event) {
        MapSession session = (MapSession) event.getOldValue();
        if (session != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Session deleted with id: " + session.getId());
            }

            this.eventPublisher.publishEvent(new SessionDeletedEvent(this, session));
        }

    }

    public void entryExpired(EntryEvent<String, MapSession> event) {
        if (logger.isDebugEnabled()) {
            logger.debug("Session expired with id: " + ((MapSession) event.getOldValue()).getId());
        }

        this.eventPublisher.publishEvent(new SessionExpiredEvent(this, (Session) event.getOldValue()));
    }

    public final class OtusHazelcastSession implements Session {
        /*
            Authentication
         */

        private final MapSession delegate;
        private boolean isNew;
        private boolean sessionIdChanged;
        private boolean lastAccessedTimeChanged;
        private boolean maxInactiveIntervalChanged;
        private String originalId;
        private Map<String, Object> delta = new HashMap();

        OtusHazelcastSession(MapSession cached, boolean isNew) {
            this.delegate = cached;
            this.isNew = isNew;
            this.originalId = cached.getId();
            if (this.isNew || HzCustomSessionRepository.this.saveMode == SaveMode.ALWAYS) {
                this.getAttributeNames().forEach((attributeName) -> {
                    this.delta.put(attributeName, cached.getAttribute(attributeName));
                });
            }

        }

        public void setLastAccessedTime(Instant lastAccessedTime) {
            this.delegate.setLastAccessedTime(lastAccessedTime);
            this.lastAccessedTimeChanged = true;
            this.flushImmediateIfNecessary();
        }

        public boolean isExpired() {
            return this.delegate.isExpired();
        }

        public Instant getCreationTime() {
            return this.delegate.getCreationTime();
        }

        public String getId() {
            return this.delegate.getId();
        }

        public String changeSessionId() {
            String newSessionId = this.delegate.changeSessionId();
            this.sessionIdChanged = true;
            return newSessionId;
        }

        public Instant getLastAccessedTime() {
            return this.delegate.getLastAccessedTime();
        }

        public void setMaxInactiveInterval(Duration interval) {
            Assert.notNull(interval, "interval must not be null");
            this.delegate.setMaxInactiveInterval(interval);
            this.maxInactiveIntervalChanged = true;
            this.flushImmediateIfNecessary();
        }

        public Duration getMaxInactiveInterval() {
            return this.delegate.getMaxInactiveInterval();
        }

        public <T> T getAttribute(String attributeName) {
            T attributeValue = this.delegate.getAttribute(attributeName);
            if (attributeValue != null && HzCustomSessionRepository.this.saveMode.equals(SaveMode.ON_GET_ATTRIBUTE)) {
                this.delta.put(attributeName, attributeValue);
            }

            return attributeValue;
        }

        public Set<String> getAttributeNames() {
            return this.delegate.getAttributeNames();
        }

        public void setAttribute(String attributeName, Object attributeValue) {
            this.delegate.setAttribute(attributeName, attributeValue);
            this.delta.put(attributeName, attributeValue);
            if (SPRING_SECURITY_CONTEXT.equals(attributeName)) {
                Map<String, String> indexes = HzCustomSessionRepository.this.indexResolver.resolveIndexesFor(this);
                String principal = attributeValue != null ? (String) indexes.get(FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME) : null;
                this.delegate.setAttribute(FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME, principal);
                this.delta.put(FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME, principal);
            }

            this.flushImmediateIfNecessary();
        }

        public void removeAttribute(String attributeName) {
            this.setAttribute(attributeName, (Object) null);
        }

        MapSession getDelegate() {
            return this.delegate;
        }

        boolean hasChanges() {
            return this.lastAccessedTimeChanged || this.maxInactiveIntervalChanged || !this.delta.isEmpty();
        }

        void clearChangeFlags() {
            this.isNew = false;
            this.lastAccessedTimeChanged = false;
            this.sessionIdChanged = false;
            this.maxInactiveIntervalChanged = false;
            this.delta.clear();
        }

        private void flushImmediateIfNecessary() {
            if (HzCustomSessionRepository.this.flushMode == FlushMode.IMMEDIATE) {
                HzCustomSessionRepository.this.save(this);
            }

        }
    }
}
