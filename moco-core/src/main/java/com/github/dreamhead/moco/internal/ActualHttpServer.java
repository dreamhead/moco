package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.HttpServer;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.setting.BaseSetting;

import java.util.ArrayList;
import java.util.List;

public class ActualHttpServer extends HttpServer {
    private final int port;
    private List<BaseSetting> settings = new ArrayList<BaseSetting>();

    public ActualHttpServer(int port) {
        this.port = port;
    }

    public List<BaseSetting> getSettings() {
        return settings;
    }

    public ResponseHandler getAnyResponseHandler() {
        return this.handler;
    }

    public int getPort() {
        return port;
    }

    @Override
    protected void addSetting(final BaseSetting setting) {
        this.settings.add(setting);
    }

    public HttpServer mergeHttpServer(HttpServer server) {
        ActualHttpServer newServer = new ActualHttpServer(this.port);
        for (BaseSetting setting : settings) {
            newServer.addSetting(setting);
        }

        ActualHttpServer thatServer = (ActualHttpServer) server;
        List<BaseSetting> thatSettings = thatServer.getSettings();
        for (BaseSetting thatSetting : thatSettings) {
            newServer.addSetting(thatSetting);
        }

        if (this.handler != null && thatServer.handler != null) {
            throw new RuntimeException("there is more than 2 any handlers");
        }

        if (this.handler == null) {
            newServer.handler = thatServer.handler;
        } else {
            newServer.handler = this.handler;
        }

        return newServer;
    }
}
