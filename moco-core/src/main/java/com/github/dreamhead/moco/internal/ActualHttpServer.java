package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.*;
import com.github.dreamhead.moco.monitor.QuietMonitor;
import com.github.dreamhead.moco.monitor.Slf4jMonitor;
import com.github.dreamhead.moco.setting.BaseSetting;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import java.util.List;

import static com.github.dreamhead.moco.util.Configs.configItem;
import static com.github.dreamhead.moco.util.Configs.configItems;
import static com.google.common.base.Optional.of;
import static com.google.common.collect.Lists.newArrayList;

public class ActualHttpServer extends HttpConfiguration {
    private Optional<Integer> port;
    private final MocoConfig[] configs;
    private final List<BaseSetting> settings = newArrayList();
    private RequestMatcher matcher = anyRequest();
    private final MocoMonitor monitor;
    protected final Optional<HttpsCertificate> certificate;

    protected ActualHttpServer(Optional<Integer> port, Optional<HttpsCertificate> certificate, MocoMonitor monitor, MocoConfig... configs) {
        this.port = port;
        this.monitor = monitor;
        this.configs = configs;
        this.certificate = certificate;
    }

    public boolean isSecure() {
        return certificate.isPresent();
    }

    public Optional<HttpsCertificate> getCertificate() {
        return certificate;
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
        ActualHttpServer newServer = newBaseServer();
        newServer.addSettings(this.getSettings());
        newServer.addSettings(thatServer.getSettings());

        newServer.anySetting(configItem(this.matcher, this.configs), configItem(this.handler, this.configs));
        newServer.anySetting(configItem(thatServer.matcher, thatServer.configs), configItem(thatServer.handler, thatServer.configs));

        newServer.addEvents(this.eventTriggers);
        newServer.addEvents(thatServer.eventTriggers);

        return newServer;
    }

    private ActualHttpServer newBaseServer() {
        if (isSecure()) {
            return createHttpsLogServer(port, certificate.get());
        }

        return createLogServer(port);
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
    protected Setting onRequestAttached(final RequestMatcher matcher) {
        BaseSetting baseSetting = new BaseSetting(matcher);
        addSetting(baseSetting);
        return baseSetting;
    }

    private static RequestMatcher anyRequest() {
        return new RequestMatcher() {
            @Override
            public boolean match(final HttpRequest request) {
                return true;
            }

            @Override
            @SuppressWarnings("unchecked")
            public RequestMatcher apply(final MocoConfig config) {
                if (config.isFor(MocoConfig.URI_ID)) {
                    return context((String)config.apply(""));
                }

                return this;
            }
        };
    }

    public static ActualHttpServer createHttpServerWithMonitor(Optional<Integer> port, MocoMonitor monitor, MocoConfig... configs) {
        return new ActualHttpServer(port, Optional.<HttpsCertificate>absent(), monitor, configs);
    }

    public static ActualHttpServer createLogServer(Optional<Integer> port, MocoConfig... configs) {
        return createHttpServerWithMonitor(port, new Slf4jMonitor(), configs);
    }

    public static ActualHttpServer createQuietServer(Optional<Integer> port, MocoConfig... configs) {
        return createHttpServerWithMonitor(port, new QuietMonitor(), configs);
    }

    public static ActualHttpServer createHttpsServerWithMonitor(Optional<Integer> port, HttpsCertificate certificate, MocoMonitor monitor, MocoConfig... configs) {
        return new ActualHttpServer(port, of(certificate), monitor, configs);
    }

    public static ActualHttpServer createHttpsLogServer(Optional<Integer> port, HttpsCertificate certificate, MocoConfig... configs) {
        return createHttpsServerWithMonitor(port, certificate, new Slf4jMonitor(), configs);
    }

    public static ActualHttpServer createHttpsQuietServer(Optional<Integer> port, HttpsCertificate certificate, MocoConfig... configs) {
        return ActualHttpServer.createHttpsServerWithMonitor(port, certificate, new QuietMonitor(), configs);
    }

    public void setPort(int port) {
        this.port = of(port);
    }
}
