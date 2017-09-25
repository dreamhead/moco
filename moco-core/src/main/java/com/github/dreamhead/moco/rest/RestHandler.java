package com.github.dreamhead.moco.rest;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.MutableHttpResponse;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.RestSetting;
import com.github.dreamhead.moco.handler.AbstractHttpResponseHandler;
import com.github.dreamhead.moco.internal.SessionContext;
import com.google.common.base.Optional;

public final class RestHandler extends AbstractHttpResponseHandler {
    private final RestRequestDispatcher dispatcher;
    private final String name;
    private final Iterable<RestSetting> settings;

    public RestHandler(final String name, final Iterable<RestSetting> settings) {
        this.name = name;
        this.dispatcher = new RestRequestDispatcher(name, settings);
        this.settings = settings;
    }

    @Override
    protected void doWriteToResponse(final HttpRequest httpRequest, final MutableHttpResponse httpResponse) {
        Optional<ResponseHandler> responseHandler = dispatcher.getResponseHandler(httpRequest);
        if (responseHandler.isPresent()) {
            responseHandler.get().writeToResponse(new SessionContext(httpRequest, httpResponse));
            return;
        }

        throw new UnsupportedOperationException("Unsupported REST request");
    }

    @Override
    @SuppressWarnings("unchecked")
    public ResponseHandler apply(final MocoConfig config) {
        if (config.isFor(MocoConfig.URI_ID)) {
            return new RestHandler((String) config.apply(name), settings);
        }

        return super.apply(config);
    }

}
