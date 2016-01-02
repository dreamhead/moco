package com.github.dreamhead.moco;

import com.github.dreamhead.moco.monitor.QuietMonitor;
import com.github.dreamhead.moco.rest.ActualRestServer;
import com.github.dreamhead.moco.rest.DeleteRestSetting;
import com.github.dreamhead.moco.rest.GetAllRestSetting;
import com.github.dreamhead.moco.rest.GetSingleRestSetting;
import com.github.dreamhead.moco.rest.HeadAllRestSetting;
import com.github.dreamhead.moco.rest.HeadSingleRestSetting;
import com.github.dreamhead.moco.rest.PostRestSetting;
import com.github.dreamhead.moco.rest.PutRestSetting;
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
        return GetAllRestSetting.builder();
    }

    public static RestSettingBuilder get(final RestIdMatcher idMatcher) {
        return GetSingleRestSetting.builder(checkNotNull(idMatcher, "ID Matcher should not be null"));
    }

    public static RestSettingBuilder post() {
        return PostRestSetting.builder();
    }

    public static RestSettingBuilder put(final String id) {
        return PutRestSetting.builder(eq(id));
    }

    public static RestSettingBuilder delete(final String id) {
        return DeleteRestSetting.builder(eq(id));
    }

    public static RestSettingBuilder head() {
        return HeadAllRestSetting.builder();
    }

    public static RestSettingBuilder head(final String id) {
        return HeadSingleRestSetting.builder(eq(id));
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
