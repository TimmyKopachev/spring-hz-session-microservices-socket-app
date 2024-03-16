package org.hz.session.integration.demo;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class ApplicationTradingServiceRunner {


    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .sources(ApplicationTradingServiceRunner.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }

}
