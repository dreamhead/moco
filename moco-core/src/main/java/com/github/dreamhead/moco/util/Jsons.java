package com.github.dreamhead.moco.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dreamhead.moco.MocoException;

public final class Jsons {
    public static String toJson(final Object value) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new MocoException(e);
        }
    }

    private Jsons() {
    }
}
