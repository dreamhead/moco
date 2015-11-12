package com.github.dreamhead.moco.rest;

import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.RestSetting;

public class GetRestSetting extends RestSetting {
    private final String id;
    private final ResponseHandler handler;

    public GetRestSetting(final String id, final ResponseHandler handler) {
        super(handler);
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
