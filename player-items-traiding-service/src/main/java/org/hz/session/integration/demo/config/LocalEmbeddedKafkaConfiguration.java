package org.hz.session.integration.demo.config;

import org.springframework.beans.factory.annotation.Value;

//@Configuration
public class LocalEmbeddedKafkaConfiguration {

//    @Value("${trading.init.topics}")
//    private String tradingTopic;

//    @Bean
//    @ConditionalOnMissingBean
//    EmbeddedKafkaBroker embeddedKafkaBroker() {
//        Objects.requireNonNull(tradingTopic);
//        var embeddedKafkaBroker = new EmbeddedKafkaZKBroker(1,
//                true,
//                2,
//                tradingTopic)
//                .zkPort(9021)
//                .kafkaPorts(9092)
//                .zkConnectionTimeout(5000)
//                .zkSessionTimeout(5000);
//
//        embeddedKafkaBroker.brokerProperty("listeners", "PLAINTEXT://localhost:9092");
//        embeddedKafkaBroker.brokerProperty("port", "9092");
//        embeddedKafkaBroker.brokerProperty("compression.type", "producer");
//
//        return embeddedKafkaBroker;
//    }
}
