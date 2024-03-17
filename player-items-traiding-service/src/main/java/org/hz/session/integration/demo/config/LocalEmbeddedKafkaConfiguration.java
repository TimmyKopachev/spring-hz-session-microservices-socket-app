package org.hz.session.integration.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.EmbeddedKafkaZKBroker;

import java.util.Objects;

@Configuration
public class LocalEmbeddedKafkaConfiguration {

    @Value("${trading.init.topics}")
    private String tradingTopic;

    @Bean
    @ConditionalOnMissingBean
    EmbeddedKafkaBroker embeddedKafkaBroker() {
        Objects.requireNonNull(tradingTopic);
        var embeddedKafkaBroker = new EmbeddedKafkaZKBroker(1,
                true,
                2,
                tradingTopic)
                .zkPort(9021)
                .kafkaPorts(9092)
                .zkConnectionTimeout(5000)
                .zkSessionTimeout(5000);

        embeddedKafkaBroker.brokerProperty("listeners", "PLAINTEXT://localhost:9092");
        embeddedKafkaBroker.brokerProperty("port", "9092");
        embeddedKafkaBroker.brokerProperty("compression.type", "producer");

        return embeddedKafkaBroker;
    }
}
