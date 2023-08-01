package com.github.dreamhead.moco.handler.cors;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MutableHttpResponse;

public interface CorsConfig {
    default boolean isSimpleRequestConfig() {
        return false;
    }

    default boolean isNonSimpleRequestConfig() {
        return false;
    }

    boolean isQualified(HttpRequest httpRequest);
    void configure(MutableHttpResponse httpResponse);
}
