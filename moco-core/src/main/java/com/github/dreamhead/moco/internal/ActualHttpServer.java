package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.*;
import com.github.dreamhead.moco.setting.BaseSetting;
import com.google.common.base.Function;
import io.netty.handler.codec.http.FullHttpRequest;

import java.util.List;

import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.Lists.newArrayList;

public class ActualHttpServer extends HttpServer {
    private final int port;
    private final MocoConfig[] configs;
    private final List<BaseSetting> settings = newArrayList();
    private RequestMatcher matcher = anyRequest();

    public ActualHttpServer(int port, MocoConfig... configs) {
        this.port = port;
        this.configs = configs;
    }

    public List<BaseSetting> getSettings() {
        return from(settings).transform(config()).toList();
    }

    private Function<BaseSetting, BaseSetting> config() {
        return new Function<BaseSetting, BaseSetting>() {
            @Override
            public BaseSetting apply(BaseSetting setting) {
                return configItem(setting);
            }
        };
    }

    private <T extends ConfigApplier<T>> T configItem(T source) {
        if (source == null) {
            return null;
        }

        T target = source;
        for (MocoConfig config : configs) {
            target = target.apply(config);
        }
        return target;
    }

    public RequestMatcher getAnyRequestMatcher() {
        return configItem(this.matcher);
    }

    public ResponseHandler getAnyResponseHandler() {
        return configItem(this.handler);
    }

    public int getPort() {
        return port;
    }

    public void addSetting(final BaseSetting setting) {
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

    @Override
    protected Setting onRequestAttached(RequestMatcher matcher) {
        return new BaseSetting(this, matcher);
    }

    @Override
    protected void onResponseAttached(ResponseHandler handler) {
    }

    private static RequestMatcher anyRequest() {
        return new RequestMatcher() {
            @Override
            public boolean match(FullHttpRequest request) {
                return true;
            }

            @Override
            public RequestMatcher apply(MocoConfig config) {
                if (config.isFor("uri")) {
                    return context(config.apply(""));
                }

                return this;
            }
        };
    }
}
