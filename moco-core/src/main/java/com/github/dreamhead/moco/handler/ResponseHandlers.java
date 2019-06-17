package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.HttpHeader;
import com.github.dreamhead.moco.MocoException;
import com.github.dreamhead.moco.MocoProcedure;
import com.github.dreamhead.moco.ResponseElement;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.collect.ImmutableMap;

import java.lang.reflect.Constructor;

import static java.lang.String.format;

public final class ResponseHandlers {
    private static final ImmutableMap<String, Class> HANDLERS = ImmutableMap.<String, Class>builder()
            .put("file", ContentHandler.class)
            .put("text", ContentHandler.class)
            .put("pathresource", ContentHandler.class)
            .put("template", ContentHandler.class)
            .put("version", VersionResponseHandler.class)
            .put("json", JsonResponseHandler.class)
            .build();

    public static ResponseHandler responseHandler(final Resource resource) {
        if (HANDLERS.containsKey(resource.id())) {
            return createResponseHandler(resource);
        }

        throw new IllegalArgumentException(format("unknown response handler for [%s]", resource.id()));
    }


    public static ResponseHandler responseHandler(final ResponseElement element) {
        if (element instanceof ResponseHandler) {
            return (ResponseHandler) element;
        }

        if (element instanceof HttpHeader) {
            return new HttpHeaderResponseHandler((HttpHeader)element);
        }

        if (element instanceof MocoProcedure) {
            return new ProcedureResponseHandler((MocoProcedure)element);
        }

        throw new IllegalArgumentException("Unknown response element:" + element.getClass());
    }

    private static ResponseHandler createResponseHandler(final Resource resource) {
        Class clazz = HANDLERS.get(resource.id());
        try {
            Constructor[] constructors = clazz.getConstructors();
            return (ResponseHandler) constructors[0].newInstance(resource);
        } catch (Exception e) {
            throw new MocoException(e);
        }
    }

    private ResponseHandlers() {
    }
}
