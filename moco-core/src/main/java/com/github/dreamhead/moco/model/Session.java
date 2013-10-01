package com.github.dreamhead.moco.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.dreamhead.moco.HttpRequest;

public class Session {
    @JsonDeserialize(as = DumpHttpRequest.class)
    private HttpRequest request;
    private Response response;

    public HttpRequest getRequest() {
        return request;
    }

    public void setRequest(HttpRequest request) {
        this.request = request;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public static Session newSession(HttpRequest request, Response response) {
        Session session = new Session();
        session.setRequest(request);
        session.setResponse(response);
        return session;
    }
}
