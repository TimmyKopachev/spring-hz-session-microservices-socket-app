package org.hz.session.integration.demo.config;

import org.springframework.beans.factory.annotation.Value;

//@Configuration
public class KafkaConfiguration {

//    @Value("${kafka.bootstrap.server:localhost:9092}")
//    private String bootstrapServers;

//    ProducerFactory<String, String> playerProductTradingProducerFactory() {
//        return new DefaultKafkaProducerFactory<>(
//                Map.of(
//                        StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,
//                        bootstrapServers,
//                        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
//                        JacksonSerializer.class,
//                        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
//                        JacksonSerializer.class));
//    }
//
//    @Bean
//    KafkaTemplate<String, String> playerProductTradingKafkaTemplate() {
//        return new KafkaTemplate<>(playerProductTradingProducerFactory());
//    }

}
