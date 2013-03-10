package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.dreamhead.moco.ResponseHandler;
import com.google.common.base.Objects;

@JsonIgnoreProperties({ "description" })
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class SessionSetting {
    private RequestSetting request;
    private ResponseSetting response;
    private String redirectTo;
    private MountSetting mount;

    public RequestSetting getRequest() {
        return request;
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
        return Objects.toStringHelper(this).omitNullValues().add("request", request).add("response", response).toString();
    }

    public boolean isRedirectResponse() {
        return redirectTo != null;
    }

    public ResponseHandler getResponseHandler() {
        return response.getResponseHandler();
    }
}
