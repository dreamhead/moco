package com.github.dreamhead.moco.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.dreamhead.moco.HttpRequest;

public class Session {
    @JsonDeserialize(as = DumpHttpRequest.class)
    private HttpRequest request;
    private Response response;

    public HttpRequest getRequest() {
        return request;
    }

    public Response getResponse() {
        return response;
    }

    @JsonCreator
    public static Session newSession(@JsonProperty("request") HttpRequest request, @JsonProperty("response") Response response) {
        Session session = new Session();
        session.request = request;
        session.response = response;
        return session;
    }
}
