package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.*;
import com.github.dreamhead.moco.monitor.MocoMonitor;
import com.github.dreamhead.moco.monitor.Slf4jMocoMonitor;
import com.github.dreamhead.moco.setting.BaseSetting;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import io.netty.handler.codec.http.FullHttpRequest;

import java.util.List;

import static com.github.dreamhead.moco.util.Configs.configItem;
import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.Lists.newArrayList;

public class ActualHttpServer extends HttpServer {
    private int port;
    private final MocoConfig[] configs;
    private final List<BaseSetting> settings = newArrayList();
    private RequestMatcher matcher = anyRequest();
    private final MocoMonitor monitor;

    private ActualHttpServer(int port, MocoMonitor monitor, MocoConfig... configs) {
        this.port = port;
        this.monitor = monitor;
        this.configs = configs;
    }

    public ImmutableList<BaseSetting> getSettings() {
        return from(settings).transform(config(configs)).toList();
    }

    public BaseSetting getAnySetting() {
        BaseSetting setting = new BaseSetting(configItem(this.matcher, configs));
        setting.response(configItem(this.handler, configs));
        for (MocoEventTrigger trigger : eventTriggers) {
            setting.on(trigger);
        }
        return setting;
    }

    public int getPort() {
        return port;
    }

    public MocoMonitor getMonitor() {
        return monitor;
    }

    private Function<BaseSetting, BaseSetting> config(final MocoConfig[] configs) {
        return new Function<BaseSetting, BaseSetting>() {
            @Override
            public BaseSetting apply(BaseSetting setting) {
                return configItem(setting, configs);
            }
        };
    }

    private void addSetting(final BaseSetting setting) {
        this.settings.add(setting);
    }

    public HttpServer mergeHttpServer(ActualHttpServer thatServer) {
        ActualHttpServer newServer = createLogServer(this.port);
        newServer.addSettings(this.getSettings());
        newServer.addSettings(thatServer.getSettings());

        newServer.anySetting(configItem(this.matcher, this.configs), configItem(this.handler, this.configs));
        newServer.anySetting(configItem(thatServer.matcher, thatServer.configs), configItem(thatServer.handler, thatServer.configs));

        return newServer;
    }

    private void anySetting(RequestMatcher matcher, ResponseHandler handler) {
        this.response(handler);
        if (handler != null) {
            this.matcher = matcher;
        }
    }

    private void addSettings(ImmutableList<BaseSetting> thatSettings) {
        for (BaseSetting thatSetting : thatSettings) {
            addSetting(thatSetting);
        }
    }

    @Override
    public int port() {
        if (port <= 0) {
            throw new IllegalStateException("unbound port should not be returned");
        }

        return this.port;
    }

    @Override
    protected Setting onRequestAttached(RequestMatcher matcher) {
        BaseSetting baseSetting = new BaseSetting(matcher);
        addSetting(baseSetting);
        return baseSetting;
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

    public static ActualHttpServer createHttpServerWithMonitor(int port, MocoMonitor monitor, MocoConfig... configs) {
        return new ActualHttpServer(port, monitor, configs);
    }

    public static ActualHttpServer createLogServer(int port, MocoConfig... configs) {
        return createHttpServerWithMonitor(port, new Slf4jMocoMonitor(), configs);
    }

    public static ActualHttpServer createSilentServer(int port, MocoConfig... configs) {
        return createHttpServerWithMonitor(port, MocoMonitor.NO_OP_MONITOR, configs);
    }

    public void setPort(int port) {
        this.port = port;
    }
}
