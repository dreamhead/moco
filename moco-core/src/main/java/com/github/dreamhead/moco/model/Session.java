package com.github.dreamhead.moco.model;

public class Session {
    private DumpHttpRequest request;
    private Response response;

    public DumpHttpRequest getRequest() {
        return request;
    }

    public void setRequest(DumpHttpRequest request) {
        this.request = request;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public static Session newSession(DumpHttpRequest request, Response response) {
        Session session = new Session();
        session.setRequest(request);
        session.setResponse(response);
        return session;
    }
}
