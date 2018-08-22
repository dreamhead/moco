package com.github.dreamhead.moco.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.HttpResponse;

public final class Session {
    @JsonDeserialize(as = DefaultHttpRequest.class)
    private HttpRequest request;
    @JsonDeserialize(as = DefaultHttpResponse.class)
    private HttpResponse response;

    public HttpRequest getRequest() {
        return request;
    }

    public HttpResponse getResponse() {
        return response;
    }

    @JsonCreator
    public static Session newSession(@JsonProperty("request") final HttpRequest request,
                                     @JsonProperty("response") final HttpResponse response) {
        Session session = new Session();
        session.request = request;
        session.response = response;
        return session;
    }
}
