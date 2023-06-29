package com.github.dreamhead.moco.handler.cors;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MutableHttpResponse;

public class CorsOriginConfig implements CorsConfig {
    private final String origin;

    public CorsOriginConfig(final String origin) {
        this.origin = origin;
    }

    @Override
    public final boolean configure(final HttpRequest httpRequest, final MutableHttpResponse httpResponse) {
        String requestOrigin = httpRequest.getHeader("Origin");
        if (origin.equals(requestOrigin) || origin.equals("*")) {
            httpResponse.addHeader("Access-Control-Allow-Origin", origin);
            return true;
        }

        return false;
    }
}
