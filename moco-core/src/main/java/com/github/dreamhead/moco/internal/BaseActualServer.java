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
import static com.google.common.collect.Lists.newArrayList;

public abstract class BaseActualServer<T extends ResponseSetting<T>, U extends BaseActualServer> extends BaseServer<T> {
    protected abstract Setting<T> newSetting(RequestMatcher matcher);

    private final MocoConfig[] configs;
    private final MocoMonitor monitor;
    private final List<Setting<T>> settings = newArrayList();
    private int port;
    private RequestMatcher anyMatcher = ANY_REQUEST_MATCHER;

    public BaseActualServer(final int port, final MocoMonitor monitor, final MocoConfig[] configs) {
        this.port = port;
        this.monitor = monitor;
        this.configs = configs;
    }

    @Override
    public final int port() {
        if (port != 0) {
            return port;
        }

        throw new IllegalStateException("unbound port should not be returned");
    }

    public final void setPort(final int port) {
        this.port = port;
    }

    public final ImmutableList<Setting<T>> getSettings() {
        return configItems(settings, configs);
    }

    public final Setting<T> getAnySetting() {
        Setting<T> setting = newSetting(configuredAnyMatcher());
        if (this.handler != null) {
            setting.response(configuredAnyResponseHandler());
        }
        for (MocoEventTrigger trigger : eventTriggers) {
            setting.on(trigger);
        }
        return setting;
    }

    protected final Optional<Integer> getPort() {
        if (port == 0) {
            return Optional.absent();
        }

        return Optional.of(port);
    }

    public final MocoMonitor getMonitor() {
        return monitor;
    }

    protected final void addSetting(final Setting<T> setting) {
        this.settings.add(setting);
    }

    protected final void addEvents(final List<MocoEventTrigger> eventTriggers) {
        this.eventTriggers.addAll(eventTriggers);
    }

    protected final void anySetting(final RequestMatcher anyMatcher, final ResponseHandler handler) {
        if (handler != null) {
            this.response(handler);
            this.anyMatcher = anyMatcher;
        }
    }

    protected final void addSettings(final ImmutableList<Setting<T>> thatSettings) {
        for (Setting<T> thatSetting : thatSettings) {
            addSetting(thatSetting);
        }
    }

    private <V extends ConfigApplier<V>> V configured(final V source) {
        return configItem(source, this.configs);
    }

    protected final RequestMatcher configuredAnyMatcher() {
        return configured(this.anyMatcher);
    }

    protected final ResponseHandler configuredAnyResponseHandler() {
        return configured(this.handler);
    }

    @SuppressWarnings("unchecked")
    public final U mergeServer(final U thatServer) {
        U newServer = createMergeServer(thatServer);
        newServer.addSettings(this.getSettings());
        newServer.addSettings(thatServer.getSettings());

        newServer.anySetting(configuredAnyMatcher(), this.configuredAnyResponseHandler());
        newServer.anySetting(thatServer.configuredAnyMatcher(), thatServer.configuredAnyResponseHandler());

        newServer.addEvents(this.eventTriggers);
        newServer.addEvents(thatServer.eventTriggers);

        return newServer;
    }

    protected abstract U createMergeServer(U thatServer);
}
