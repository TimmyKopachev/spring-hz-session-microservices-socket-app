package org.hz.session.integration.demo.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class JacksonDeserializer<T> implements Deserializer<T> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Class<T> clazz;

    public JacksonDeserializer(Class<T> clazz) {
        this.clazz = clazz;
    }


    @Override
    public T deserialize(String topic, byte[] data) {
        try (InputStream inputStream = new ByteArrayInputStream(data)) {
            return objectMapper.readValue(inputStream, clazz);
        } catch (IOException exception) {
            throw new SerializationException(exception);
        }
    }
}
