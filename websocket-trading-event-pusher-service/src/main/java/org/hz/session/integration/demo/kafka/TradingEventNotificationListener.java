package org.hz.session.integration.demo.kafka;

import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.session.SessionRepository;
import org.springframework.session.hazelcast.HazelcastIndexedSessionRepository;
import org.springframework.stereotype.Component;

import static org.springframework.session.FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME;

@Component
@AllArgsConstructor
public class TradingEventNotificationListener {

    final SimpMessagingTemplate messageSocketTemplate;
    final SessionRepository sessionRepository;
    final HazelcastIndexedSessionRepository hazelcastSessionRepository;

    @KafkaListener(id = "socket-event-notification-handler", topics = "socket-trading-event-notification-topic")
    @SendTo("/channel/requests")
    public void eventHandler(String product) {
        var sessions = hazelcastSessionRepository.findByIndexNameAndIndexValue(PRINCIPAL_NAME_INDEX_NAME, "username-3");
//        if (!sessions.entrySet().isEmpty()) {
//            sessions.keySet()
//                    .forEach(sessionId ->
//                            messageSocketTemplate.convertAndSendToUser(sessionId, "/channel/requests", product));
//        }
        if (!sessions.entrySet().isEmpty()) {
            sessions.keySet()
                    .forEach(sessionId ->
                            messageSocketTemplate.convertAndSend(
                                    String.format("/user/%s/channel/requests", sessionId), product));
        }
    }

}
