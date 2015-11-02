package com.github.dreamhead.moco.rest;

import com.github.dreamhead.moco.HttpsCertificate;
import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.MocoMonitor;
import com.github.dreamhead.moco.RestServer;
import com.github.dreamhead.moco.handler.JsonResponseHandler;
import com.github.dreamhead.moco.internal.ActualHttpServer;
import com.github.dreamhead.moco.internal.InternalApis;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import io.netty.handler.codec.http.HttpResponseStatus;

import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.status;
import static com.github.dreamhead.moco.Moco.uri;
import static com.github.dreamhead.moco.util.URLs.join;
import static com.github.dreamhead.moco.util.URLs.resourceRoot;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.copyOf;

public class ActualRestServer extends ActualHttpServer implements RestServer {
    public ActualRestServer(final Optional<Integer> port,
                               final Optional<HttpsCertificate> certificate,
                               final MocoMonitor monitor,
                               final MocoConfig... configs) {
        super(port, certificate, monitor, configs);
    }

    @Override
    public void resource(final String name, final RestSetting... settings) {
        checkNotNull(name, "Resource name should not be null");
        checkNotNull(settings, "Rest settings should not be null");

        for (RestSetting setting : settings) {
            this.get(by(uri(join(resourceRoot(name), setting.getId())))).response(setting.getHandler());
        }

        FluentIterable<? extends RestSetting> handlers = FluentIterable.from(copyOf(settings));
        if (handlers.allMatch(isJsonHandlers())) {
            ImmutableList<Object> objects = handlers.transform(toJsonHandler()).transform(toPojo()).toList();
            this.get(by(uri(resourceRoot(name)))).response(Moco.toJson(objects));
        }

        this.get(InternalApis.context(resourceRoot(name))).response(status(HttpResponseStatus.NOT_FOUND.code()));
    }

    private Function<JsonResponseHandler, Object> toPojo() {
        return new Function<JsonResponseHandler, Object>() {
            @Override
            public Object apply(final JsonResponseHandler handler) {
                return handler.getPojo();
            }
        };
    }

    private Function<RestSetting, JsonResponseHandler> toJsonHandler() {
        return new Function<RestSetting, JsonResponseHandler>() {
            @Override
            public JsonResponseHandler apply(final RestSetting setting) {
                return JsonResponseHandler.class.cast(setting.getHandler());
            }
        };
    }

    private Predicate<RestSetting> isJsonHandlers() {
        return new Predicate<RestSetting>() {
            @Override
            public boolean apply(final RestSetting setting) {
                return setting.getHandler() instanceof JsonResponseHandler;
            }
        };
    }
}
