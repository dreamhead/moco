package com.github.dreamhead.moco.handler.cors;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MutableHttpResponse;

public final class CorsMaxAgeConfig implements CorsConfig {
    private final long maxAge;

    public CorsMaxAgeConfig(final long maxAge) {
        this.maxAge = maxAge;
    }

    @Override
    public boolean isQualified(final HttpRequest httpRequest) {
        return true;
    }

    @Override
    public void configure(final MutableHttpResponse httpResponse) {
        httpResponse.addHeader("Access-Control-Max-Age", String.valueOf(maxAge));
    }
}
