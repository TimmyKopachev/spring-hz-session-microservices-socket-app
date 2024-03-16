package org.hz.session.integration.demo.config;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientConnectionStrategyConfig;
import com.hazelcast.core.HazelcastInstance;
import lombok.SneakyThrows;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration

//@EnableHazelcastHttpSession
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

        return HazelcastClient.getOrCreateHazelcastClient(clientConfig);
    }

}
