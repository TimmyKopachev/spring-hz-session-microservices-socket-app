package org.hz.session.integration.demo.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class JacksonSerializer<T> implements Serializer<T> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(String topic, T data) {
        try (ByteArrayOutputStream outputStream = prepareOutputStream(data)) {
            return outputStream.toByteArray();
        } catch (IOException exception) {
            throw new SerializationException(exception);
        }
    }

    private ByteArrayOutputStream prepareOutputStream(T data) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            objectMapper.writeValue(outputStream, data);
            return outputStream;
        }
    }
}
