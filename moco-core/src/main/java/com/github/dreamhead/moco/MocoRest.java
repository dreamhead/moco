package com.github.dreamhead.moco;

import com.github.dreamhead.moco.monitor.QuietMonitor;
import com.github.dreamhead.moco.rest.ActualRestServer;
import com.github.dreamhead.moco.rest.BaseRestSettingBuilder;
import com.github.dreamhead.moco.rest.RestIdMatchers;
import com.google.common.base.Optional;

import static com.github.dreamhead.moco.rest.RestIdMatchers.eq;
import static com.github.dreamhead.moco.util.Preconditions.checkNotNullOrEmpty;
import static com.google.common.base.Optional.of;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class MocoRest {
    public static RestServer restServer(final int port, final MocoConfig... configs) {
        checkArgument(port > 0, "Port must be greater than zero");
        checkNotNull(configs, "Config should not be null");
        return new ActualRestServer(of(port), Optional.<HttpsCertificate>absent(), new QuietMonitor(), configs);
    }

    public static RestServer restServer(final int port, final MocoMonitor monitor, final MocoConfig... configs) {
        checkArgument(port > 0, "Port must be greater than zero");
        checkNotNull(configs, "Config should not be null");
        return new ActualRestServer(of(port), Optional.<HttpsCertificate>absent(),
                checkNotNull(monitor, "Monitor should not be null"), configs);
    }

    public static RestIdMatcher anyId() {
        return RestIdMatchers.anyId();
    }

    public static RestSettingBuilder get(final String id) {
        return get(eq(checkId(id)));
    }

    public static RestSettingBuilder get() {
        return BaseRestSettingBuilder.all(HttpMethod.GET);
    }

    public static RestSettingBuilder get(final RestIdMatcher idMatcher) {
        return BaseRestSettingBuilder.single(HttpMethod.GET, checkNotNull(idMatcher, "ID Matcher should not be null"));
    }

    public static RestSettingBuilder post() {
        return BaseRestSettingBuilder.all(HttpMethod.POST);
    }

    public static RestSettingBuilder put(final String id) {
        return BaseRestSettingBuilder.single(HttpMethod.PUT, eq(checkId(id)));
    }

    public static RestSettingBuilder delete(final String id) {
        return BaseRestSettingBuilder.single(HttpMethod.DELETE, eq(checkId(id)));
    }

    public static RestSettingBuilder head() {
        return BaseRestSettingBuilder.all(HttpMethod.HEAD);
    }

    public static RestSettingBuilder head(final String id) {
        return BaseRestSettingBuilder.single(HttpMethod.HEAD, eq(checkId(id)));
    }

    private static String checkId(final String id) {
        checkNotNullOrEmpty(id, "ID should not be null or empty");
        if (id.contains("/")) {
            throw new IllegalArgumentException("REST ID should not contain '/'");
        }

        return id;
    }

    private MocoRest() {
    }
}
