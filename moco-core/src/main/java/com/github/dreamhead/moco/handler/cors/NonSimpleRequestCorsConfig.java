package com.github.dreamhead.moco.handler.cors;

public interface NonSimpleRequestCorsConfig extends CorsConfig {
    default boolean isNonSimpleRequestConfig() {
        return true;
    }
}
