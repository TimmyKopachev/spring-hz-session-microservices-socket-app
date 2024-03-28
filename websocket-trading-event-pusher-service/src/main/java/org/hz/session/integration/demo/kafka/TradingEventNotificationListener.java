package org.hz.session.integration.demo.kafka;

import lombok.AllArgsConstructor;
import org.hz.session.integration.demo.model.channel.TradeRequest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TradingEventNotificationListener {

    final SimpMessageSendingOperations messagingTemplate;

    @KafkaListener(id = "socket-event-notification-handler", topics = "socket-trading-event-notification-topic")
    public void eventHandler(TradeRequest request) {
        messagingTemplate.convertAndSendToUser(request.getUsernameTo(), "/queue/requests", request.getProduct());
    }

    private MessageHeaders createHeaders(String sessionId) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor
                .create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);
        return headerAccessor.getMessageHeaders();
    }

}
