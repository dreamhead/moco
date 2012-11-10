package com.github.dreamhead.moco.parser.model;

public class SessionSetting {
    private RequestSetting request;
    private ResponseSetting response;

    public ResponseSetting getResponse() {
        return response;
    }

    public RequestSetting getRequest() {
        return request;
    }

    public boolean isAnyResponse() {
        return request == null;
    }
}
