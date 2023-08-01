package com.github.dreamhead.moco.handler.cors;

public interface SimpleRequestCorsConfig extends CorsConfig {
    default boolean isSimpleRequestConfig() {
        return true;
    }
}
