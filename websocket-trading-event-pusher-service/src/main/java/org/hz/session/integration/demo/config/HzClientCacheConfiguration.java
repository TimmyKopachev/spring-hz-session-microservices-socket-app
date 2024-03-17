package org.hz.session.integration.demo.config;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientConnectionStrategyConfig;
import com.hazelcast.config.SerializerConfig;
import com.hazelcast.core.HazelcastInstance;
import lombok.SneakyThrows;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.MapSession;
import org.springframework.session.hazelcast.HazelcastSessionSerializer;
import org.springframework.session.hazelcast.config.annotation.web.http.EnableHazelcastHttpSession;

@Configuration
@EnableHazelcastHttpSession
public class HzClientCacheConfiguration extends CachingConfigurerSupport {

    @SneakyThrows
    @Bean
    public HazelcastInstance hazelcastInstance() {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setInstanceName("hz-trading-instance");
        clientConfig.setClusterName("DEV");
        clientConfig.getNetworkConfig()
                .addAddress("127.0.0.1:5701");

        clientConfig.getConnectionStrategyConfig()
                .setAsyncStart(true)
                .setReconnectMode(ClientConnectionStrategyConfig.ReconnectMode.ASYNC);

        SerializerConfig serializerConfig = new SerializerConfig();
        serializerConfig.setImplementation(new HazelcastSessionSerializer()).setTypeClass(MapSession.class);
        clientConfig.getSerializationConfig().addSerializerConfig(serializerConfig);

        return HazelcastClient.getOrCreateHazelcastClient(clientConfig);
    }

}
