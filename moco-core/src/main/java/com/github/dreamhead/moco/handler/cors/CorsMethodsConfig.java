package com.github.dreamhead.moco.handler.cors;

import com.github.dreamhead.moco.HttpMethod;
import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MutableHttpResponse;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;

public class CorsMethodsConfig implements CorsConfig {
    private final String[] methods;

    public CorsMethodsConfig(final String[] methods) {
        this.methods = methods;
        ;
    }

    @Override
    public void configure(final HttpRequest httpRequest, final MutableHttpResponse httpResponse) {
        String requestOrigin = httpRequest.getHeader("Origin");
        if (!Strings.isNullOrEmpty(requestOrigin)) {
            HttpMethod method = httpRequest.getMethod();
            for (String m : methods) {
                if (method.name().equalsIgnoreCase(m)) {
                    httpResponse.addHeader("Access-Control-Allow-Methods", Joiner.on(",").join(methods));
                    return;
                }
            }
        } else {
            httpResponse.addHeader("Access-Control-Allow-Methods", Joiner.on(",").join(methods));
        }
    }
}
