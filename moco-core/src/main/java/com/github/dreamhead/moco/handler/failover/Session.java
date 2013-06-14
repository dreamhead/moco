package com.github.dreamhead.moco.handler.failover;

public class Session {
    private Request request;
    private Response response;

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public static Session newSession(Request request, Response response) {
        Session session = new Session();
        session.setRequest(request);
        session.setResponse(response);
        return session;
    }
}
