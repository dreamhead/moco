package com.github.dreamhead.moco.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.dreamhead.moco.MocoException;
import com.google.common.collect.ImmutableList;
import com.google.common.io.CharStreams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.google.common.collect.ImmutableList.of;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.lang.String.format;

public final class Jsons {
    private static Logger logger = LoggerFactory.getLogger(Jsons.class);

    private static final TypeFactory DEFAULT_FACTORY = TypeFactory.defaultInstance();
    private static final ObjectMapper DEFAULT_MAPPER = new ObjectMapper();

    public static String toJson(final Object value) {
        try {
            return DEFAULT_MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new MocoException(e);
        }
    }

    public static String toJson(final Map<?, ?> map) {
        try {
            return DEFAULT_MAPPER.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new MocoException(e);
        }
    }

    public static <T> T toObject(final String value, final Class<T> clazz) {
        try {
            return DEFAULT_MAPPER.readValue(value, clazz);
        } catch (IOException e) {
            throw new MocoException(e);
        }
    }

    public static <T> T toObject(final InputStream value, final Class<T> clazz) {
        try {
            return DEFAULT_MAPPER.readValue(value, clazz);
        } catch (IOException e) {
            throw new MocoException(e);
        }
    }

    public static <T> T toObject(final Reader value, final Class<T> clazz) {
        try {
            return DEFAULT_MAPPER.readValue(value, clazz);
        } catch (IOException e) {
            throw new MocoException(e);
        }
    }

    public static <T> ImmutableList<T> toObjects(final String value, final Class<T> elementClass) {
        return toObjects(new ByteArrayInputStream(value.getBytes()), elementClass);
    }

    public static <T> ImmutableList<T> toObjects(final InputStream stream, final Class<T> elementClass) {
        return toObjects(of(stream), elementClass);
    }

    public static <T> ImmutableList<T> toObjects(final ImmutableList<InputStream> streams,
                                                 final Class<T> elementClass) {
        final CollectionType type = DEFAULT_FACTORY.constructCollectionType(List.class, elementClass);
        return streams.stream()
                .flatMap(Jsons.<T>toObject(type))
                .collect(toImmutableList());
    }

    private static <T> Function<InputStream, Stream<T>> toObject(final CollectionType type) {
        return input -> {
            try (InputStream actual = input) {
                String text = CharStreams.toString(new InputStreamReader(actual));
                return DEFAULT_MAPPER.<List<T>>readValue(text, type).stream();
            } catch (UnrecognizedPropertyException e) {
                logger.info("Unrecognized field: {}", e.getMessage());
                throw new MocoException(format("Unrecognized field [ %s ], please check!", e.getPropertyName()));
            } catch (JsonMappingException e) {
                logger.info("{} {}", e.getMessage(), e.getPathReference());
                throw new MocoException(e);
            } catch (IOException e) {
                throw new MocoException(e);
            }
        };
    }

    public static void writeToFile(final File file, final Object value) {
        ObjectWriter writer = DEFAULT_MAPPER.writerWithDefaultPrettyPrinter();
        try {
            writer.writeValue(file, value);
        } catch (IOException e) {
            throw new MocoException(e);
        }
    }

    public static void writeToFile(final Path file, final Object value) {
        writeToFile(file.toFile(), value);
    }

    private Jsons() {
    }
}
