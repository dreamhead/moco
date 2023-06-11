package com.github.dreamhead.moco.handler.cors;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MutableHttpResponse;
import com.google.common.base.Joiner;

public class CorsMethodsConfig implements CorsConfig {
    private final String methods;

    public CorsMethodsConfig(final String[] methods) {
        this.methods = Joiner.on(",").join(methods);
    }

    @Override
    public void configure(HttpRequest httpRequest, final MutableHttpResponse httpResponse) {
        httpResponse.addHeader("Access-Control-Allow-Methods", methods);
    }
}
