package com.github.dreamhead.moco.parser.model;

import com.github.dreamhead.moco.ResponseHandler;

public abstract class RestBaseSetting {
    private ResponseSetting response;

    protected ResponseHandler getResponseHandler() {
        return response.getResponseHandler();
    }
}
