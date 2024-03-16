package org.hz.session.integration.demo.kafka;

import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

//@Component
//@AllArgsConstructor
public class TradingEventNotificationListener {

    //final SimpMessagingTemplate messageSocketTemplate;

    @KafkaListener(id = "socket-event-notification-handler", topics = "socket-trading-event-notification-topic")
    public void eventHandler(String product) {
        System.out.println(product);
        //messageSocketTemplate.convertAndSendToUser("username-3", "/channel", product);
    }

}
