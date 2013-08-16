package com.github.dreamhead.moco.model;

public class Session {
    private DefaultRequest request;
    private Response response;

    public DefaultRequest getRequest() {
        return request;
    }

    public void setRequest(DefaultRequest request) {
        this.request = request;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public static Session newSession(DefaultRequest request, Response response) {
        Session session = new Session();
        session.setRequest(request);
        session.setResponse(response);
        return session;
    }
}
