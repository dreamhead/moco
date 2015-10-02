package com.github.dreamhead.moco.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class Jsons {
    public static String toJson(final Object value) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private Jsons() {
    }
}
