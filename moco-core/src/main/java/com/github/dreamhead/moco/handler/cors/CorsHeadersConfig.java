package com.github.dreamhead.moco.handler.cors;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MutableHttpResponse;
import com.google.common.base.Joiner;

public class CorsHeadersConfig implements CorsConfig {
    private final String headers;

    public CorsHeadersConfig(final String[] headers) {
        this.headers = Joiner.on(',').join(headers);
    }

    @Override
    public void configure(final HttpRequest httpRequest, final MutableHttpResponse httpResponse) {
        httpResponse.addHeader("Access-Control-Allow-Headers", headers);
    }
}
