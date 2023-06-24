package com.github.dreamhead.moco.handler.cors;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MutableHttpResponse;

public class CorsExposedHeadersConfig implements CorsConfig {
    private final String[] headers;

    public CorsExposedHeadersConfig(final String[] headers) {
        this.headers = headers;
    }


    @Override
    public void configure(HttpRequest httpRequest, MutableHttpResponse httpResponse) {
        httpResponse.addHeader("Access-Control-Expose-Headers", String.join(",", headers));
    }
}
