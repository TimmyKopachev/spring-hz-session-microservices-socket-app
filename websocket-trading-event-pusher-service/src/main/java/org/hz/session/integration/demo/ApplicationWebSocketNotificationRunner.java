package org.hz.session.integration.demo;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
//import org.springframework.kafka.annotation.EnableKafka;
//import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
//@EnableAsync
//@EnableKafka
public class ApplicationWebSocketNotificationRunner {

    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .sources(ApplicationWebSocketNotificationRunner.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }

}
