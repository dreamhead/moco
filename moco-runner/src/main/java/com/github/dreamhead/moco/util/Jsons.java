package com.github.dreamhead.moco.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Jsons {
    public static String toJson(Object value) {
        ObjectMapper mapper = new ObjectMapper();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            mapper.writeValue(baos, value);
            return baos.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
