package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.HttpHeader;
import com.github.dreamhead.moco.MocoException;
import com.github.dreamhead.moco.MocoProcedure;
import com.github.dreamhead.moco.ResponseElement;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.resource.Resource;

import java.lang.reflect.Constructor;
import java.util.Map;

public final class ResponseHandlers {
    private static final Map<String, Class<?>> HANDLERS = Map.of(
            "file", ContentHandler.class,
            "text", ContentHandler.class,
            "pathresource", ContentHandler.class,
            "binary", ContentHandler.class,
            "template", ContentHandler.class,
            "version", VersionResponseHandler.class,
            "json", JsonResponseHandler.class);

    public static ResponseHandler responseHandler(final ResponseElement element) {
        if (element instanceof ResponseHandler handler) {
            return handler;
        }

        if (element instanceof Resource resource) {
            return responseHandler(resource);
        }

        if (element instanceof HttpHeader header) {
            return new HttpHeaderResponseHandler(header);
        }

        if (element instanceof MocoProcedure procedure) {
            return new ProcedureResponseHandler(procedure);
        }

        throw new IllegalArgumentException("Unknown response element:" + element.getClass());
    }

    private static ResponseHandler responseHandler(final Resource resource) {
        if (HANDLERS.containsKey(resource.id())) {
            return createResponseHandler(resource);
        }

        throw new IllegalArgumentException("unknown response handler for [%s]".formatted(resource.id()));
    }

    private static ResponseHandler createResponseHandler(final Resource resource) {
        Class<?> clazz = HANDLERS.get(resource.id());
        try {
            Constructor<?>[] constructors = clazz.getConstructors();
            return (ResponseHandler) constructors[0].newInstance(resource);
        } catch (Exception e) {
            throw new MocoException(e);
        }
    }

    private ResponseHandlers() {
    }
}
