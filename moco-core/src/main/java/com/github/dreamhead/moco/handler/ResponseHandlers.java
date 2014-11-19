package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.collect.ImmutableMap;

import java.lang.reflect.Constructor;

import static java.lang.String.format;

public class ResponseHandlers {
    private static final ImmutableMap<String, Class> handlers = ImmutableMap.<String, Class>builder()
            .put("file", ContentHandler.class)
            .put("text", ContentHandler.class)
            .put("pathresource", ContentHandler.class)
            .put("template", ContentHandler.class)
            .put("fromrequest", ContentHandler.class)
            .put("version", VersionResponseHandler.class).build();

    public static ResponseHandler responseHandler(Resource resource) {
        if (handlers.containsKey(resource.id())) {
            return createResponseHandler(resource);
        }

        throw new IllegalArgumentException(format("unknown response handler for [%s]", resource.id()));
    }

    private static ResponseHandler createResponseHandler(Resource resource) {
        Class clazz = handlers.get(resource.id());
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
