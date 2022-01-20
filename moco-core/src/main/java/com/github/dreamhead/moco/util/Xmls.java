package com.github.dreamhead.moco.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.github.dreamhead.moco.MocoException;

public final class Xmls {
    private static final XmlMapper DEFAULT_MAPPER = new XmlMapper();

    static {
        DEFAULT_MAPPER.setVisibility(
                DEFAULT_MAPPER.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY));
    }

    public static <T> T toObject(final String text, final Class<T> clazz) {
        try {
            return DEFAULT_MAPPER.readValue(text, clazz);
        } catch (JsonProcessingException e) {
            throw new MocoException(e);
        }
    }

    private Xmls() {
    }
}
