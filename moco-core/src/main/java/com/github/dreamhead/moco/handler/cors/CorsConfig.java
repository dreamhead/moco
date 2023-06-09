package com.github.dreamhead.moco.handler.cors;

import com.github.dreamhead.moco.MutableHttpResponse;

public interface CorsConfig {
    void configure(MutableHttpResponse httpResponse);
}
