package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.*;
import com.github.dreamhead.moco.matcher.AndRequestMatcher;
import com.github.dreamhead.moco.setting.BaseSetting;
import org.jboss.netty.handler.codec.http.HttpRequest;

import java.util.List;

import static com.google.common.collect.ImmutableList.of;
import static com.google.common.collect.Lists.newArrayList;

public class ActualHttpServer extends HttpServer {
    private final int port;
    private final MocoConfig[] configs;
    private List<BaseSetting> settings = newArrayList();
    private RequestMatcher matcher = anyRequest();

    public ActualHttpServer(int port, MocoConfig... configs) {
        this.port = port;
        this.configs = configs;
    }

    public List<BaseSetting> getSettings() {
        return settings;
    }

    public RequestMatcher getAnyRequestMatcher() {
        return this.matcher;
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
            RequestMatcher appliedMatcher = this.matcher.apply(config);
            if (config.isFor("uri") && this.matcher == appliedMatcher) {
                appliedMatcher = new AndRequestMatcher(of(appliedMatcher, context(config.apply(""))));
            }

            this.matcher = appliedMatcher;
            this.handler = this.handler.apply(config);
        }
    }

    private static RequestMatcher anyRequest() {
        return new RequestMatcher() {
            @Override
            public boolean match(HttpRequest request) {
                return true;
            }

            @Override
            public RequestMatcher apply(MocoConfig config) {
                return this;
            }
        };
    }
}
