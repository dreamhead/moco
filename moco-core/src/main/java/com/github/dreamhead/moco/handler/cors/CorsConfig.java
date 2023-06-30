package com.github.dreamhead.moco.handler.cors;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MutableHttpResponse;

public interface CorsConfig {
    boolean isQualified(HttpRequest httpRequest);
    void configure(MutableHttpResponse httpResponse);
}
