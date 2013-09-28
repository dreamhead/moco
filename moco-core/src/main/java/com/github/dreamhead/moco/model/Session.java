package com.github.dreamhead.moco.model;

public class Session {
    private DefaultHttpRequest request;
    private Response response;

    public DefaultHttpRequest getRequest() {
        return request;
    }

    public void setRequest(DefaultHttpRequest request) {
        this.request = request;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public static Session newSession(DefaultHttpRequest request, Response response) {
        Session session = new Session();
        session.setRequest(request);
        session.setResponse(response);
        return session;
    }
}
