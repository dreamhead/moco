package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.*;
import com.github.dreamhead.moco.monitor.QuietMonitor;
import com.github.dreamhead.moco.monitor.Slf4jMonitor;
import com.github.dreamhead.moco.setting.BaseSetting;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import io.netty.handler.codec.http.FullHttpRequest;

import java.util.List;

import static com.github.dreamhead.moco.util.Configs.configItem;
import static com.github.dreamhead.moco.util.Configs.configItems;
import static com.google.common.base.Optional.of;
import static com.google.common.collect.Lists.newArrayList;

public class ActualHttpServer extends HttpServer {
    private Optional<Integer> port;
    private final MocoConfig[] configs;
    private final List<BaseSetting> settings = newArrayList();
    private RequestMatcher matcher = anyRequest();
    private final MocoMonitor monitor;

    private ActualHttpServer(Optional<Integer> port, MocoMonitor monitor, MocoConfig... configs) {
        this.port = port;
        this.monitor = monitor;
        this.configs = configs;
    }

    public ImmutableList<BaseSetting> getSettings() {
        return configItems(settings, configs);
    }

    public BaseSetting getAnySetting() {
        BaseSetting setting = new BaseSetting(configItem(this.matcher, configs));
        ResponseHandler configuredHandler = configItem(this.handler, configs);
        if (configuredHandler != null) {
            setting.response(configuredHandler);
        }
        for (MocoEventTrigger trigger : eventTriggers) {
            setting.on(trigger);
        }
        return setting;
    }

    public Optional<Integer> getPort() {
        return port;
    }

    public MocoMonitor getMonitor() {
        return monitor;
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

        newServer.addEvents(this.eventTriggers);
        newServer.addEvents(thatServer.eventTriggers);

        return newServer;
    }

    private void addEvents(List<MocoEventTrigger> eventTriggers) {
        this.eventTriggers.addAll(eventTriggers);
    }

    private void anySetting(RequestMatcher matcher, ResponseHandler handler) {
        if (handler != null) {
            this.response(handler);
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
        if (port.isPresent()) {
            return port.get();
        }

        throw new IllegalStateException("unbound port should not be returned");
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

    public static ActualHttpServer createHttpServerWithMonitor(Optional<Integer> port, MocoMonitor monitor, MocoConfig... configs) {
        return new ActualHttpServer(port, monitor, configs);
    }

    public static ActualHttpServer createLogServer(Optional<Integer> port, MocoConfig... configs) {
        return createHttpServerWithMonitor(port, new Slf4jMonitor(), configs);
    }

    public static ActualHttpServer createQuietServer(Optional<Integer> port, MocoConfig... configs) {
        return createHttpServerWithMonitor(port, new QuietMonitor(), configs);
    }

    public void setPort(int port) {
        this.port = of(port);
    }
}
