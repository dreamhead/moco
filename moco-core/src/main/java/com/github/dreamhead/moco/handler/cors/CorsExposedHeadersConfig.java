package com.github.dreamhead.moco.handler.cors;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MutableHttpResponse;

public final class CorsExposedHeadersConfig implements CorsConfig {
    private final String headers;

    public CorsExposedHeadersConfig(final String[] headers) {
        this.headers = String.join(",", headers);
    }


    @Override
    public boolean isQualified(final HttpRequest httpRequest) {
        return true;
    }

    @Override
    public void configure(final MutableHttpResponse httpResponse) {
        httpResponse.addHeader("Access-Control-Expose-Headers", headers);
    }
}
