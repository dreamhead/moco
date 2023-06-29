package com.github.dreamhead.moco.handler.cors;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MutableHttpResponse;

public class CorsHeadersConfig implements CorsConfig {
    private final String headers;

    public CorsHeadersConfig(final String[] headers) {
        this.headers = String.join(",", headers);
    }

    @Override
    public final boolean configure(final HttpRequest httpRequest, final MutableHttpResponse httpResponse) {
        httpResponse.addHeader("Access-Control-Allow-Headers", headers);
        return true;
    }
}
