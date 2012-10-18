package com.github.moco;

public abstract class RequestSetting {
    protected abstract void addToServer(MocoServer server);

    protected MocoServer server;
    protected String response;

    public RequestSetting(MocoServer server) {
        this.server = server;
    }

    public void response(String response) {
        this.response = response;
        addToServer(server);
    }
}
