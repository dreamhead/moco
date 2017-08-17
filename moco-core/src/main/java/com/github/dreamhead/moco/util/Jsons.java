package com.github.dreamhead.moco.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.dreamhead.moco.MocoException;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.io.Closeables.closeQuietly;
import static java.lang.String.format;

public final class Jsons {
    private static Logger logger = LoggerFactory.getLogger(Jsons.class);

    private final static TypeFactory factory = TypeFactory.defaultInstance();
    private final static ObjectMapper mapper = new ObjectMapper();

    public static String toJson(final Object value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new MocoException(e);
        }
    }

    public static <T> T toObject(final InputStream value, final Class<T> clazz) {
        try {
            return mapper.readValue(value, clazz);
        } catch (IOException e) {
            throw new MocoException(e);
        }
    }

    public static <T> T toObject(final String value, final Class<T> clazz) {
        try {
            return mapper.readValue(value, clazz);
        } catch (IOException e) {
            throw new MocoException(e);
        }
    }

    public static <T> ImmutableList<T> toObjects(final String value, final Class<T> elementClass) {
        return toObjects(new ByteArrayInputStream(value.getBytes()), elementClass);
    }

    public static <T> ImmutableList<T> toObjects(final InputStream stream, final Class<T> elementClass) {
        try {
            CollectionType type = factory.constructCollectionType(List.class, elementClass);
            List<T> sessionSettings = mapper.readValue(stream, type);
            return copyOf(sessionSettings);
        } catch (UnrecognizedPropertyException e) {
            logger.info("Unrecognized field: {}", e.getMessage());
            throw new RuntimeException(format("Unrecognized field [ %s ], please check!", e.getPropertyName()));
        } catch (JsonMappingException e) {
            logger.info("{} {}", e.getMessage(), e.getPathReference());
            throw new MocoException(e);
        } catch (IOException e) {
            throw new MocoException(e);
        } finally {
            closeQuietly(stream);
        }
    }

    private Jsons() {
    }
}
