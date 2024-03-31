package org.hz.session.integration.demo.kafka;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hz.session.integration.demo.config.HzCustomSessionRepository;
import org.hz.session.integration.demo.model.channel.TradeRequest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class TradingEventNotificationListener {

    final SimpMessageSendingOperations messagingTemplate;
    final HzCustomSessionRepository sessionRepository;

    @KafkaListener(id = "socket-event-notification-handler", topics = "socket-trading-event-notification-topic")
    public void eventHandler(TradeRequest request) {
        log.debug("Authentication: <{}>", SecurityContextHolder.getContext().getAuthentication());
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
