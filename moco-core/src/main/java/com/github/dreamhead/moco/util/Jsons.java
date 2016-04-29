package com.github.dreamhead.moco.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dreamhead.moco.MocoException;

import java.io.IOException;
import java.io.InputStream;

public final class Jsons {
    private static ObjectMapper mapper = new ObjectMapper();

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

    public static <T> T toObject(final String value, final TypeReference clazz) {
        try {
            return mapper.readValue(value, clazz);
        } catch (IOException e) {
            throw new MocoException(e);
        }
    }

    public static <T> T toObject(final InputStream value, final TypeReference clazz) {
        try {
            return mapper.readValue(value, clazz);
        } catch (IOException e) {
            throw new MocoException(e);
        }
    }

    private Jsons() {
    }
}
