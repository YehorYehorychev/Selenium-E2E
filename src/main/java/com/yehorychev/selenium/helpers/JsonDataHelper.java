package com.yehorychev.selenium.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Centralized JSON helper backed by a shared ObjectMapper instance. Supports loading test fixtures
 * from classpath resources or absolute paths and writing POJOs back to disk when diagnostics are
 * required.
 */
public final class JsonDataHelper {

    private static final Logger log = LoggerFactory.getLogger(JsonDataHelper.class);
    private static final ObjectMapper MAPPER = buildMapper();

    private JsonDataHelper() {
        // utility class
    }

    private static ObjectMapper buildMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        return mapper;
    }

    public static <T> T readResource(String resourcePath, Class<T> targetClass) {
        log.debug("Reading JSON resource {} into {}", resourcePath, targetClass.getSimpleName());
        try (InputStream inputStream = getResourceAsStream(resourcePath)) {
            return MAPPER.readValue(inputStream, targetClass);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read JSON resource: " + resourcePath, e);
        }
    }

    public static <T> T readResource(String resourcePath, TypeReference<T> typeReference) {
        log.debug("Reading JSON resource {} into generic type", resourcePath);
        try (InputStream inputStream = getResourceAsStream(resourcePath)) {
            return MAPPER.readValue(inputStream, typeReference);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read JSON resource: " + resourcePath, e);
        }
    }

    public static <T> T readFile(Path path, Class<T> targetClass) {
        log.debug("Reading JSON file {} into {}", path, targetClass.getSimpleName());
        try {
            return MAPPER.readValue(path.toFile(), targetClass);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read JSON file: " + path, e);
        }
    }

    public static <T> T readFile(Path path, TypeReference<T> typeReference) {
        log.debug("Reading JSON file {} into generic type", path);
        try {
            return MAPPER.readValue(path.toFile(), typeReference);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read JSON file: " + path, e);
        }
    }

    public static void writeFile(Path path, Object data) {
        log.debug("Writing JSON file {}", path);
        try {
            Files.createDirectories(path.getParent());
            MAPPER.writeValue(path.toFile(), data);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to write JSON file: " + path, e);
        }
    }

    public static String toJson(Object value) {
        try {
            return MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize object to JSON", e);
        }
    }

    public static List<Map<String, Object>> readResourceAsList(String resourcePath) {
        return readResource(resourcePath, new TypeReference<>() {});
    }

    @SneakyThrows
    private static InputStream getResourceAsStream(String resourcePath) {
        InputStream inputStream = JsonDataHelper.class.getClassLoader().getResourceAsStream(resourcePath);
        if (inputStream == null) {
            throw new IOException("Resource not found: " + resourcePath);
        }
        return inputStream;
    }
}
