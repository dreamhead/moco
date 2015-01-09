package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.*;
import com.github.dreamhead.moco.matcher.AbstractRequestMatcher;
import com.github.dreamhead.moco.setting.Setting;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import java.util.List;

import static com.github.dreamhead.moco.util.Configs.configItem;
import static com.github.dreamhead.moco.util.Configs.configItems;
import static com.google.common.base.Optional.of;
import static com.google.common.collect.Lists.newArrayList;

public abstract class BaseActualServer <T extends ResponseSetting<T>> extends BaseServer<T> {
    protected abstract Setting<T> newSetting(final RequestMatcher matcher);

    protected final MocoConfig[] configs;
    protected final MocoMonitor monitor;
    private final List<Setting<T>> settings = newArrayList();
    protected Optional<Integer> port;
    protected RequestMatcher matcher = anyRequest();

    public BaseActualServer(Optional<Integer> port, MocoMonitor monitor, MocoConfig[] configs) {
        this.port = port;
        this.monitor = monitor;
        this.configs = configs;
    }

    private static RequestMatcher anyRequest() {
        return new AbstractRequestMatcher() {
            @Override
            public boolean match(final Request request) {
                return true;
            }

            @Override
            @SuppressWarnings("unchecked")
            public RequestMatcher doApply(final MocoConfig config) {
                if (config.isFor(MocoConfig.REQUEST_ID)) {
                    return (RequestMatcher)config.apply(anyRequest());
                }

                return this;
            }
        };
    }

    public int port() {
        if (port.isPresent()) {
            return port.get();
        }

        throw new IllegalStateException("unbound port should not be returned");
    }

    public void setPort(int port) {
        this.port = of(port);
    }

    public ImmutableList<Setting<T>> getSettings() {
        return configItems(settings, configs);
    }

    public Setting<T> getAnySetting() {
        Setting<T> setting = newSetting(configItem(this.matcher, configs));
        ResponseHandler configuredHandler = configItem(this.handler, configs);
        if (configuredHandler != null) {
            setting.response(configuredHandler);
        }
        for (MocoEventTrigger trigger : eventTriggers) {
            setting.on(trigger);
        }
        return setting;
    }

    protected Optional<Integer> getPort() {
        return port;
    }

    public MocoMonitor getMonitor() {
        return monitor;
    }

    protected void addSetting(final Setting<T> setting) {
        this.settings.add(setting);
    }

    protected void addEvents(List<MocoEventTrigger> eventTriggers) {
        this.eventTriggers.addAll(eventTriggers);
    }

    protected void anySetting(RequestMatcher matcher, ResponseHandler handler) {
        if (handler != null) {
            this.response(handler);
            this.matcher = matcher;
        }
    }

    protected void addSettings(ImmutableList<Setting<T>> thatSettings) {
        for (Setting<T> thatSetting : thatSettings) {
            addSetting(thatSetting);
        }
    }
}
