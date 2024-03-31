package org.hz.session.integration.demo.config;

import com.hazelcast.map.EntryProcessor;
import com.hazelcast.map.ExtendedMapEntry;
import org.springframework.session.MapSession;

import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HzCustomSessionUpdateEntryProcessor implements EntryProcessor<String, MapSession, Object> {

    private Instant lastAccessedTime;
    private Duration maxInactiveInterval;
    private Map<String, Object> delta;

    public HzCustomSessionUpdateEntryProcessor() {
    }

    public Object process(Map.Entry<String, MapSession> entry) {
        MapSession value = (MapSession) entry.getValue();
        if (value == null) {
            return Boolean.FALSE;
        } else {
            if (this.lastAccessedTime != null) {
                value.setLastAccessedTime(this.lastAccessedTime);
            }

            if (this.maxInactiveInterval != null) {
                value.setMaxInactiveInterval(this.maxInactiveInterval);
            }

            if (this.delta != null) {
                Iterator var3 = this.delta.entrySet().iterator();

                while (var3.hasNext()) {
                    Map.Entry<String, Object> attribute = (Map.Entry) var3.next();
                    if (attribute.getValue() != null) {
                        value.setAttribute((String) attribute.getKey(), attribute.getValue());
                    } else {
                        value.removeAttribute((String) attribute.getKey());
                    }
                }
            }

            ((ExtendedMapEntry) entry).setValue(value, value.getMaxInactiveInterval().getSeconds(), TimeUnit.SECONDS);
            return Boolean.TRUE;
        }
    }

    void setLastAccessedTime(Instant lastAccessedTime) {
        this.lastAccessedTime = lastAccessedTime;
    }

    void setMaxInactiveInterval(Duration maxInactiveInterval) {
        this.maxInactiveInterval = maxInactiveInterval;
    }

    void setDelta(Map<String, Object> delta) {
        this.delta = delta;
    }

}
