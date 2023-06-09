package com.github.dreamhead.moco.handler.cors;

import com.github.dreamhead.moco.MutableHttpResponse;

public class CorsOriginConfig implements CorsConfig {
    private final String origin;

    public CorsOriginConfig(final String origin) {
        this.origin = origin;
    }

    @Override
    public void configure(MutableHttpResponse httpResponse) {
        httpResponse.addHeader("Access-Control-Allow-Origin", origin);
    }
}
