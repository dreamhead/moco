package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MutableHttpResponse;
import com.github.dreamhead.moco.handler.cors.CorsConfig;

public class MocoCorsHandler extends AbstractHttpResponseHandler {
    private final CorsConfig[] configs;

    public MocoCorsHandler(final CorsConfig[] configs) {
        this.configs = configs;
    }

    @Override
    protected void doWriteToResponse(final HttpRequest httpRequest, final MutableHttpResponse httpResponse) {
        if (configs.length == 0) {
            httpResponse.addHeader("Access-Control-Allow-Origin", "*");
            httpResponse.addHeader("Access-Control-Allow-Methods", "*");
            httpResponse.addHeader("Access-Control-Allow-Headers", "*");
            return;
        }

        for (CorsConfig config : configs) {
            config.configure(httpResponse);
        }
    }
}
