package com.github.dreamhead.moco.parser.model;

import com.github.dreamhead.moco.ResponseHandler;
import com.google.common.base.Objects;

import java.io.IOException;

public class SessionSetting {
    private RequestSetting request;
    private ResponseSetting response;
    private String redirectTo;
    private MountSetting mount;

    public RequestSetting getRequest() {
        return request;
    }

    public ResponseSetting getResponse() {
        return response;
    }

    public String getRedirectTo() {
        return redirectTo;
    }

    public MountSetting getMount() {
        return mount;
    }

    public boolean isMount() {
        return this.mount != null;
    }

    public boolean isAnyResponse() {
        return request == null;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("request", request).add("response", response).toString();
    }

    public boolean isRedirectResponse() {
        return redirectTo != null;
    }

    public ResponseHandler getResponseHandler() throws IOException {
        return response.getResponseHandler();
    }
}
