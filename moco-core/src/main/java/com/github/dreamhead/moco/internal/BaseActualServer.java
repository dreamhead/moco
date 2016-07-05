package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.ConfigApplier;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.MocoEventTrigger;
import com.github.dreamhead.moco.MocoMonitor;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.ResponseSetting;
import com.github.dreamhead.moco.setting.Setting;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import java.util.List;

import static com.github.dreamhead.moco.RequestMatcher.ANY_REQUEST_MATCHER;
import static com.github.dreamhead.moco.util.Configs.configItem;
import static com.github.dreamhead.moco.util.Configs.configItems;
import static com.google.common.base.Optional.of;
import static com.google.common.collect.Lists.newArrayList;

public abstract class BaseActualServer<T extends ResponseSetting<T>, U extends BaseActualServer> extends BaseServer<T> {
    protected abstract Setting<T> newSetting(final RequestMatcher matcher);

    private final MocoConfig[] configs;
    private final MocoMonitor monitor;
    private final List<Setting<T>> settings = newArrayList();
    private Optional<Integer> port;
    private RequestMatcher matcher = ANY_REQUEST_MATCHER;

    public BaseActualServer(final Optional<Integer> port, final MocoMonitor monitor, final MocoConfig[] configs) {
        this.port = port;
        this.monitor = monitor;
        this.configs = configs;
    }

    @Override
    public int port() {
        if (port.isPresent()) {
            return port.get();
        }

        throw new IllegalStateException("unbound port should not be returned");
    }

    public void setPort(final int port) {
        this.port = of(port);
    }

    public ImmutableList<Setting<T>> getSettings() {
        return configItems(settings, configs);
    }

    public Setting<T> getAnySetting() {
        Setting<T> setting = newSetting(configuredMatcher());
        if (this.handler != null) {
            setting.response(configuredResponseHandler());
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

    protected void addEvents(final List<MocoEventTrigger> eventTriggers) {
        this.eventTriggers.addAll(eventTriggers);
    }

    protected void anySetting(final RequestMatcher matcher, final ResponseHandler handler) {
        if (handler != null) {
            this.response(handler);
            this.matcher = matcher;
        }
    }

    protected void addSettings(final ImmutableList<Setting<T>> thatSettings) {
        for (Setting<T> thatSetting : thatSettings) {
            addSetting(thatSetting);
        }
    }

    protected  <V extends ConfigApplier<V>> V configured(final V source) {
        return configItem(source, this.configs);
    }

    protected RequestMatcher configuredMatcher() {
        return configured(this.matcher);
    }

    protected ResponseHandler configuredResponseHandler() {
        return configured(this.handler);
    }

    public U mergeServer(final U thatServer) {
        U newServer = createMergeServer(thatServer);
        newServer.addSettings(this.getSettings());
        newServer.addSettings(thatServer.getSettings());

        newServer.anySetting(configuredMatcher(), this.configuredResponseHandler());
        newServer.anySetting(thatServer.configuredMatcher(), thatServer.configuredResponseHandler());

        newServer.addEvents(this.eventTriggers);
        newServer.addEvents(thatServer.eventTriggers);

        return newServer;
    }

    protected abstract U createMergeServer(final U thatServer);
}
