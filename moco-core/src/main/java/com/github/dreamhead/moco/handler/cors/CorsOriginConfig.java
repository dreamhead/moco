package com.github.dreamhead.moco.handler.cors;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MutableHttpResponse;

public final class CorsOriginConfig implements CorsConfig {
    private final String origin;

    public CorsOriginConfig(final String origin) {
        this.origin = origin;
    }

    @Override
    public boolean isQualified(final HttpRequest httpRequest) {
        String requestOrigin = httpRequest.getHeader("Origin");
        return origin.equals(requestOrigin) || origin.equals("*");
    }

    @Override
    public void configure(final MutableHttpResponse httpResponse) {
        httpResponse.addHeader("Access-Control-Allow-Origin", origin);
    }

    @Override
    public boolean isSimpleRequestConfig() {
        return true;
    }

    @Override
    public boolean isNonSimpleRequestConfig() {
        return true;
    }
}
