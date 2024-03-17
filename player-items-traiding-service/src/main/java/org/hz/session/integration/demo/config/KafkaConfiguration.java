package org.hz.session.integration.demo.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.hz.session.integration.demo.serialization.JacksonSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Map;

@Configuration
public class KafkaConfiguration {

    @Value("${kafka.bootstrap.server:localhost:9092}")
    private String bootstrapServers;

    ProducerFactory<String, String> playerProductTradingProducerFactory() {
        return new DefaultKafkaProducerFactory<>(
                Map.of(
                        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                        bootstrapServers,
                        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                        JacksonSerializer.class,
                        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                        JacksonSerializer.class));
    }

    @Bean
    KafkaTemplate<String, String> playerProductTradingKafkaTemplate() {
        return new KafkaTemplate<>(playerProductTradingProducerFactory());
    }

}
