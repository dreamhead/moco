package com.github.dreamhead.moco;

import com.github.dreamhead.moco.monitor.QuietMonitor;
import com.github.dreamhead.moco.rest.ActualRestServer;
import com.github.dreamhead.moco.rest.RestIdMatchers;
import com.github.dreamhead.moco.rest.SubResourceSetting;
import com.google.common.base.Optional;

import static com.github.dreamhead.moco.rest.RestIdMatchers.eq;
import static com.github.dreamhead.moco.rest.RestSettingBuilders.all;
import static com.github.dreamhead.moco.rest.RestSettingBuilders.single;
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

    public static RestSetting resource(final String id, final String name,
                                       final RestSetting... settings) {
        return new SubResourceSetting(checkId(id),
                checkNotNullOrEmpty(name, "Resource name should not be null or empty"),
                checkNotNull(settings, "Rest settings should not be null"));
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
