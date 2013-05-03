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

    public HttpServer mergeHttpServer(ActualHttpServer thatServer) {
        ActualHttpServer newServer = new ActualHttpServer(this.port);
        newServer.addSettings(settings);
        newServer.addSettings(thatServer.getSettings());
        newServer.response(this.handler);
        newServer.response(thatServer.handler);
        return newServer;
    }

    private void addSettings(List<BaseSetting> thatSettings) {
        for (BaseSetting thatSetting : thatSettings) {
            addSetting(thatSetting);
        }
    }
}
