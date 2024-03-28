package org.hz.session.integration.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
@Slf4j
public class ApplicationWebSocketNotificationRunner {

    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .sources(ApplicationWebSocketNotificationRunner.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }

}
