package com.github.dreamhead.moco.rest;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.MutableHttpResponse;
import com.github.dreamhead.moco.handler.AbstractHttpResponseHandler;
import com.github.dreamhead.moco.handler.JsonResponseHandler;
import com.github.dreamhead.moco.internal.SessionContext;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import io.netty.handler.codec.http.HttpResponseStatus;

import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.status;
import static com.github.dreamhead.moco.Moco.uri;
import static com.github.dreamhead.moco.util.URLs.join;
import static com.github.dreamhead.moco.util.URLs.resourceRoot;
import static com.google.common.collect.ImmutableList.copyOf;

public class RestHandler extends AbstractHttpResponseHandler {
    private final String name;
    private final RestSetting[] settings;

    public RestHandler(final String name, final RestSetting... settings) {
        this.name = name;
        this.settings = settings;
    }

    @Override
    protected void doWriteToResponse(final HttpRequest httpRequest, final MutableHttpResponse httpResponse) {
        if ("get".equalsIgnoreCase(httpRequest.getMethod())) {
            writeGetResponse(httpRequest, httpResponse);
            return;
        }

        throw new UnsupportedOperationException("Unsupported REST request");

    }

    private void writeGetResponse(final HttpRequest httpRequest, final MutableHttpResponse httpResponse) {
        for (RestSetting setting : settings) {
            if (by(uri(join(resourceRoot(name), setting.getId()))).match(httpRequest)) {
                setting.getHandler().writeToResponse(new SessionContext(httpRequest, httpResponse));
                return;
            }
        }

        if (by(uri(resourceRoot(name))).match(httpRequest)) {
            FluentIterable<? extends RestSetting> handlers = FluentIterable.from(copyOf(settings));
            if (handlers.allMatch(isJsonHandlers())) {
                ImmutableList<Object> objects = handlers.transform(toJsonHandler()).transform(toPojo()).toList();
                Moco.toJson(objects).writeToResponse(new SessionContext(httpRequest, httpResponse));
                return;
            }
        }

        status(HttpResponseStatus.NOT_FOUND.code()).writeToResponse(new SessionContext(httpRequest, httpResponse));
    }

    private Function<JsonResponseHandler, Object> toPojo() {
        return new Function<JsonResponseHandler, Object>() {
            @Override
            public Object apply(final JsonResponseHandler handler) {
                return handler.getPojo();
            }
        };
    }

    private Function<RestSetting, JsonResponseHandler> toJsonHandler() {
        return new Function<RestSetting, JsonResponseHandler>() {
            @Override
            public JsonResponseHandler apply(final RestSetting setting) {
                return JsonResponseHandler.class.cast(setting.getHandler());
            }
        };
    }

    private Predicate<RestSetting> isJsonHandlers() {
        return new Predicate<RestSetting>() {
            @Override
            public boolean apply(final RestSetting setting) {
                return setting.getHandler() instanceof JsonResponseHandler;
            }
        };
    }
}
