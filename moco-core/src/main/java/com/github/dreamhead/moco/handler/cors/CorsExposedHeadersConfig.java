package com.github.dreamhead.moco.handler.cors;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MutableHttpResponse;

public class CorsExposedHeadersConfig implements CorsConfig {
    private final String[] headers;

    public CorsExposedHeadersConfig(final String[] headers) {
        this.headers = headers;
    }


    @Override
    public boolean isQualified(HttpRequest httpRequest) {
        return true;
    }

    @Override
    public final void configure(final MutableHttpResponse httpResponse) {
        httpResponse.addHeader("Access-Control-Expose-Headers", String.join(",", headers));
    }
}
