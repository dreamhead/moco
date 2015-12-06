package com.github.dreamhead.moco;

import com.github.dreamhead.moco.monitor.QuietMonitor;
import com.github.dreamhead.moco.rest.ActualRestServer;
import com.github.dreamhead.moco.rest.DeleteRestSetting;
import com.github.dreamhead.moco.rest.GetAllRestSetting;
import com.github.dreamhead.moco.rest.GetSingleRestSetting;
import com.github.dreamhead.moco.rest.HeadRestSetting;
import com.github.dreamhead.moco.rest.PostRestSetting;
import com.github.dreamhead.moco.rest.PutRestSetting;
import com.google.common.base.Optional;

import static com.github.dreamhead.moco.handler.AndResponseHandler.and;
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

    public static RestSetting get(final String id, final ResponseHandler handler) {
        return new GetSingleRestSetting(checkNotNullOrEmpty(id, "ID should not be null or empty"),
                Optional.<RequestMatcher>absent(),
                checkNotNull(handler, "Get response handler should not be null"));
    }

    public static RestSetting get(final String id, final RequestMatcher matcher, final ResponseHandler handler) {
        return new GetSingleRestSetting(checkNotNullOrEmpty(id, "ID should not be null or empty"),
                Optional.of(checkNotNull(matcher, "Get request matcher should no be null")),
                checkNotNull(handler, "Get response handler should not be null"));
    }

    public static RestSetting get(final ResponseHandler handler) {
        return new GetAllRestSetting(Optional.<RequestMatcher>absent(),
                checkNotNull(handler, "Get response handler should not be null"));
    }

    public static RestSetting get(final RequestMatcher matcher, final ResponseHandler handler) {
        return new GetAllRestSetting(of(checkNotNull(matcher, "Get request matcher should no be null")),
                checkNotNull(handler, "Get response handler should not be null"));
    }

    public static RestSetting post(final ResponseHandler... handlers) {
        return new PostRestSetting(and(handlers));
    }

    public static RestSetting put(final String id, final RequestMatcher matcher, final ResponseHandler... handlers) {
        return new PutRestSetting(checkNotNullOrEmpty(id, "ID should not be null or empty"),
                Optional.of(checkNotNull(matcher, "Put request matcher should no be null")),
                and(checkNotNull(handlers, "Put response handler should not be null")));
    }

    public static RestSetting put(final String id, final ResponseHandler... handlers) {
        return new PutRestSetting(checkNotNullOrEmpty(id, "ID should not be null or empty"),
                Optional.<RequestMatcher>absent(),
                and(checkNotNull(handlers, "Put response handler should not be null")));
    }

    public static RestSetting delete(final String id, final ResponseHandler... handlers) {
        return new DeleteRestSetting(checkNotNullOrEmpty(id, "ID should not be null or empty"),
                Optional.<RequestMatcher>absent(),
                and(checkNotNull(handlers, "Delete response handler should not be null")));
    }

    public static RestSetting delete(final String id, final RequestMatcher matcher, final ResponseHandler... handlers) {
        return new DeleteRestSetting(checkNotNullOrEmpty(id, "ID should not be null or empty"),
                of(checkNotNull(matcher, "Delete request matcher should be not null")),
                and(checkNotNull(handlers, "Delete response handler should not be null")));
    }

    public static RestSetting head(final String id, final ResponseHandler... handlers) {
        return new HeadRestSetting(checkNotNullOrEmpty(id, "ID should not be null or empty"),
                Optional.<RequestMatcher>absent(),
                and(checkNotNull(handlers, "Head response handler should not be null")));
    }

    public static RestSetting head(final String id, final RequestMatcher matcher, final ResponseHandler... handlers) {
        return new HeadRestSetting(checkNotNullOrEmpty(id, "ID should not be null or empty"),
                of(checkNotNull(matcher, "Head matcher should be not null")),
                and(checkNotNull(handlers, "Head response handler should not be null")));
    }

    private MocoRest() {
    }
}
