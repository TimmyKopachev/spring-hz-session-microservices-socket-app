package org.hz.session.integration.demo.serialization;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;

public final class CommonStreamConfig {

    private CommonStreamConfig() {
        throw new IllegalStateException();
    }

    public static <T> Serde<T> getSerde(Class<T> clazz) {
        final JacksonSerializer<T> serializer = new JacksonSerializer<>();
        final JacksonDeserializer<T> deserializer = new JacksonDeserializer<>(clazz);
        return Serdes.serdeFrom(serializer, deserializer);
    }
}
