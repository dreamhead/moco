package com.github.dreamhead.moco;

import com.github.dreamhead.moco.monitor.QuietMonitor;
import com.github.dreamhead.moco.rest.ActualRestServer;
import com.github.dreamhead.moco.rest.RestIdMatchers;
import com.github.dreamhead.moco.rest.builder.ActualSubResourceSettingBuilder;
import com.github.dreamhead.moco.rest.builder.SubResourceSettingBuilder;

import static com.github.dreamhead.moco.internal.ApiUtils.mergeMonitor;
import static com.github.dreamhead.moco.rest.RestIdMatchers.eq;
import static com.github.dreamhead.moco.rest.RestIds.checkId;
import static com.github.dreamhead.moco.rest.builder.RestSettingBuilders.all;
import static com.github.dreamhead.moco.rest.builder.RestSettingBuilders.single;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class MocoRest {
    public static RestServer restServer(final int port, final MocoConfig... configs) {
        checkArgument(port > 0, "Port must be greater than zero");
        checkNotNull(configs, "Config should not be null");
        return new ActualRestServer(port, null, new QuietMonitor(), configs);
    }

    public static RestServer restServer(final int port, final MocoMonitor monitor, final MocoConfig... configs) {
        checkArgument(port > 0, "Port must be greater than zero");
        checkNotNull(configs, "Config should not be null");
        return new ActualRestServer(port, null,
                checkNotNull(monitor, "Monitor should not be null"), configs);
    }

    public static RestServer restServer(final int port, final MocoMonitor monitor,
                                        final MocoMonitor monitor2, final MocoMonitor... monitors) {
        checkArgument(port > 0, "Port must be greater than zero");
        return new ActualRestServer(port, null,
                mergeMonitor(checkNotNull(monitor, "Monitor should not be null"),
                        checkNotNull(monitor2, "Monitor should not be null"), monitors));
    }

    public static RestIdMatcher anyId() {
        return RestIdMatchers.anyId();
    }

    public static SubResourceSettingBuilder id(final String id) {
        return new ActualSubResourceSettingBuilder(eq(checkId(id)));
    }

    public static SubResourceSettingBuilder id(final RestIdMatcher id) {
        return new ActualSubResourceSettingBuilder(checkNotNull(id, "ID matcher should not be null"));
    }

    public static RestSettingBuilder get(final String id) {
        return get(eq(checkId(id)));
    }

    public static RestSettingBuilder get() {
        return all(HttpMethod.GET);
    }

    public static RestSettingBuilder get(final RestIdMatcher idMatcher) {
        return single(HttpMethod.GET, checkNotNull(idMatcher, "ID Matcher should not be null"));
    }

    public static RestSettingBuilder post() {
        return all(HttpMethod.POST);
    }

    public static RestSettingBuilder put(final RestIdMatcher idMatcher) {
        return single(HttpMethod.PUT, checkNotNull(idMatcher, "ID Matcher should not be null"));
    }

    public static RestSettingBuilder put(final String id) {
        return put(eq(checkId(id)));
    }

    public static RestSettingBuilder delete(final RestIdMatcher idMatcher) {
        return single(HttpMethod.DELETE, checkNotNull(idMatcher, "ID Matcher should not be null"));
    }

    public static RestSettingBuilder delete(final String id) {
        return delete(eq(checkId(id)));
    }

    public static RestSettingBuilder head() {
        return all(HttpMethod.HEAD);
    }

    public static RestSettingBuilder head(final RestIdMatcher idMatcher) {
        return single(HttpMethod.HEAD, checkNotNull(idMatcher, "ID Matcher should not be null"));
    }

    public static RestSettingBuilder head(final String id) {
        return head(eq(checkId(id)));
    }

    public static RestSettingBuilder patch(final RestIdMatcher idMatcher) {
        return single(HttpMethod.PATCH, checkNotNull(idMatcher, "ID Matcher should not be null"));
    }

    public static RestSettingBuilder patch(final String id) {
        return patch(eq(checkId(id)));
    }

    private MocoRest() {
    }
}
