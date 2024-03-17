package org.hz.session.integration.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableKafka
@Slf4j
public class ApplicationWebSocketNotificationRunner {

    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .sources(ApplicationWebSocketNotificationRunner.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }
//
//    @Override
//    public void run(ApplicationArguments args) {
//        WebSocketClient client = new StandardWebSocketClient();
//
//
//        SockJsClient sockJsClient = new SockJsClient(
//                List.of(new WebSocketTransport(client))
//        );
//
//        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
//        var auth = String.format("%s:%s", "username-1","password");
//        headers.add("Authorization", "Basic " + new String(Base64.getEncoder().encode(auth.getBytes())));
//
//        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
//        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
//        stompClient.connect(SERVER_SOCKET_URL, headers, new StompSessionHandlerAdapter() {
//            @Override
//            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
//                session.subscribe("/channel/requests", this);
//            }
//
//            @Override
//            public void handleFrame(StompHeaders headers, Object payload) {
//                System.out.println("==============");
//                log.debug("handle frame headers: {}", headers);
//                log.debug("handle frame payload: {}", payload);
//            }
//        });
//    }
}
