package com.github.dreamhead.moco.handler.cors;

import com.github.dreamhead.moco.HttpMethod;
import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MutableHttpResponse;

import java.util.Arrays;
import java.util.stream.Collectors;

public final class CorsMethodsConfig implements NonSimpleRequestCorsConfig {
    private final HttpMethod[] methods;

    public CorsMethodsConfig(final HttpMethod[] methods) {
        this.methods = methods;
    }

    @Override
    public boolean isQualified(final HttpRequest httpRequest) {
        HttpMethod method = httpRequest.getMethod();
        return method.equals(HttpMethod.OPTIONS) || "*".equals(methods[0]) || Arrays.stream(methods).anyMatch(m -> method == m);
    }

    @Override
    public void configure(final MutableHttpResponse httpResponse) {
        httpResponse.addHeader("Access-Control-Allow-Methods",
                Arrays.stream(methods).map(Enum::toString).collect(Collectors.joining(",")));

    }
}
