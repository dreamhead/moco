package com.github.dreamhead.moco.util;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.exc.UnrecognizedPropertyException;
import tools.jackson.databind.type.CollectionType;
import tools.jackson.databind.type.TypeFactory;
import com.github.dreamhead.moco.MocoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.List.of;

public final class Jsons {
    private static Logger logger = LoggerFactory.getLogger(Jsons.class);

    private static final ObjectMapper DEFAULT_MAPPER = new ObjectMapper();
    private static final TypeFactory DEFAULT_FACTORY = DEFAULT_MAPPER.getTypeFactory();

    public static String toJson(final Object value) {
        try {
            return DEFAULT_MAPPER.writeValueAsString(value);
        } catch (JacksonException e) {
            throw new MocoException(e);
        }
    }

    public static String toJson(final Map<?, ?> map) {
        try {
            return DEFAULT_MAPPER.writeValueAsString(map);
        } catch (JacksonException e) {
            throw new MocoException(e);
        }
    }

    public static <T> T toObject(final String value, final Class<T> clazz) {
        try {
            return DEFAULT_MAPPER.readValue(value, clazz);
        } catch (JacksonException e) {
            throw new MocoException(e);
        }
    }

    public static <T> T toObject(final InputStream value, final Class<T> clazz) {
        try {
            return DEFAULT_MAPPER.readValue(value, clazz);
        } catch (JacksonException e) {
            throw new MocoException(e);
        }
    }

    public static <T> T toObject(final Reader value, final Class<T> clazz) {
        try {
            return DEFAULT_MAPPER.readValue(value, clazz);
        } catch (JacksonException e) {
            throw new MocoException(e);
        }
    }

    public static <T> List<T> toObjects(final String value, final Class<T> elementClass) {
        return toObjects(new ByteArrayInputStream(value.getBytes()), elementClass);
    }

    public static <T> List<T> toObjects(final InputStream stream, final Class<T> elementClass) {
        return toObjects(of(stream), elementClass);
    }

    public static <T> List<T> toObjects(final List<InputStream> streams,
                                                 final Class<T> elementClass) {
        final CollectionType type = DEFAULT_FACTORY.constructCollectionType(List.class, elementClass);
        return streams.stream()
                .flatMap(Jsons.<T>toObject(type))
                .toList();
    }

    private static <T> Function<InputStream, Stream<T>> toObject(final CollectionType type) {
        return input -> {
            try (InputStream actual = input) {
                String text = new String(actual.readAllBytes());
                return DEFAULT_MAPPER.<List<T>>readValue(text, type).stream();
            } catch (UnrecognizedPropertyException e) {
                logger.info("Unrecognized field: {}", e.getMessage());
                throw new MocoException("Unrecognized field [ %s ], please check!".formatted(e.getPropertyName()));
            } catch (DatabindException e) {
                logger.info("{} {}", e.getMessage(), e.getPathReference());
                throw new MocoException(e);
            } catch (IOException e) {
                throw new MocoException(e);
            } catch (JacksonException e) {
                throw new MocoException(e);
            }
        };
    }

    public static void writeToFile(final File file, final Object value) {
        ObjectWriter writer = DEFAULT_MAPPER.writerWithDefaultPrettyPrinter();
        try {
            writer.writeValue(file, value);
        } catch (JacksonException e) {
            throw new MocoException(e);
        }
    }

    public static void writeToFile(final Path file, final Object value) {
        writeToFile(file.toFile(), value);
    }

    private Jsons() {
    }
}
