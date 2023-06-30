package com.github.dreamhead.moco.handler.cors;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MutableHttpResponse;

public class CorsHeadersConfig implements CorsConfig {
    private final String headers;

    public CorsHeadersConfig(final String[] headers) {
        this.headers = String.join(",", headers);
    }

    @Override
    public boolean isQualified(final HttpRequest httpRequest) {
        return true;
    }

    @Override
    public final void configure(final MutableHttpResponse httpResponse) {
        httpResponse.addHeader("Access-Control-Allow-Headers", headers);
    }
}
