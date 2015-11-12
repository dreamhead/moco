package com.github.dreamhead.moco;

public class RestSetting {
    private final ResponseHandler handler;

    public RestSetting(final ResponseHandler handler) {
        this.handler = handler;
    }

    public ResponseHandler getHandler() {
        return handler;
    }
}
