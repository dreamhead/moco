package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.collect.ImmutableMap;

import java.lang.reflect.Constructor;

import static java.lang.String.format;

public class ResponseHandlers {
    private static ImmutableMap<String, Class> handlers = ImmutableMap.<String, Class>builder()
            .put("file", ContentHandler.class)
            .put("text", ContentHandler.class)
            .put("pathresource", ContentHandler.class)
            .put("url", ContentHandler.class)
            .put("version", VersionResponseHandler.class).build();

    public static ResponseHandler responseHandler(Resource resource) {
        Class clazz = handlers.get(resource.id());
        if (clazz == null) {
            throw new RuntimeException(format("unknown extractor for [%s]", resource.id()));
        }

        try {
            Constructor[] constructors = clazz.getConstructors();
            return (ResponseHandler)constructors[0].newInstance(resource);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ResponseHandlers() {
    }

}
