package com.github.dreamhead.moco.rest;

import com.github.dreamhead.moco.ResponseHandler;

public class RestSetting {
    private final String id;
    private final ResponseHandler handler;

    public RestSetting(final String id, final ResponseHandler handler) {
        this.id = id;
        this.handler = handler;
    }

    public String getId() {
        return id;
    }

    public ResponseHandler getHandler() {
        return handler;
    }
}
