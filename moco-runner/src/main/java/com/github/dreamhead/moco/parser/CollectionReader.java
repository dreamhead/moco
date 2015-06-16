package com.github.dreamhead.moco.parser;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.dreamhead.moco.HttpServer;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.io.Closeables.closeQuietly;
import static java.lang.String.format;

public class CollectionReader {
    private static Logger logger = LoggerFactory.getLogger(HttpServer.class);

    private final ObjectMapper mapper = new ObjectMapper();
    private final TypeFactory factory = TypeFactory.defaultInstance();

    public CollectionReader(final Module... modules) {
        for (Module module : modules) {
            mapper.registerModule(module);
        }
    }

    public <T> ImmutableList<T> read(final InputStream is, final Class<T> elementClass) {
        try {
            CollectionType type = factory.constructCollectionType(List.class, elementClass);
            List<T> sessionSettings = mapper.readValue(is, type);
            return copyOf(sessionSettings);
        } catch (UnrecognizedPropertyException e) {
            logger.info("Unrecognized field: {}", e.getMessage());
            throw new RuntimeException(format("Unrecognized field [ %s ], please check!", e.getPropertyName()));
        } catch (JsonMappingException e) {
            logger.info("{} {}", e.getMessage(), e.getPathReference());
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            closeQuietly(is);
        }
    }
}
