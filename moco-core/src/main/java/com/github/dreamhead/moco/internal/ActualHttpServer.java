package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.*;
import com.github.dreamhead.moco.setting.BaseSetting;

import java.util.ArrayList;
import java.util.List;

public class ActualHttpServer extends HttpServer {
    private final int port;
    private final MocoConfig[] configs;
    private List<BaseSetting> settings = new ArrayList<BaseSetting>();

    public ActualHttpServer(int port, MocoConfig... configs) {
        this.port = port;
        this.configs = configs;
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

    public void addSetting(final BaseSetting setting) {
        BaseSetting configSetting = setting;
        for (MocoConfig config : configs) {
            configSetting = configSetting.apply(config);
        }

        this.settings.add(configSetting);
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

    @Override
    protected Setting onRequestAttached(RequestMatcher matcher) {
        return new BaseSetting(this, matcher);
    }

    @Override
    protected void onResponseAttached(ResponseHandler handler) {
        for (MocoConfig config : configs) {
            this.handler.apply(config);
        }
    }
}
