package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.HttpServer;
import com.github.dreamhead.moco.HttpsCertificate;
import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.MocoMonitor;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.dumper.HttpRequestDumper;
import com.github.dreamhead.moco.dumper.HttpResponseDumper;
import com.github.dreamhead.moco.handler.JsonResponseHandler;
import com.github.dreamhead.moco.monitor.QuietMonitor;
import com.github.dreamhead.moco.monitor.Slf4jMonitor;
import com.github.dreamhead.moco.setting.HttpSetting;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;

import java.util.Map;

import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.uri;
import static com.github.dreamhead.moco.util.URLs.join;
import static com.github.dreamhead.moco.util.URLs.resourceRoot;
import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.FluentIterable.from;

public class ActualHttpServer extends HttpConfiguration {
    private final Optional<HttpsCertificate> certificate;

    protected ActualHttpServer(final Optional<Integer> port,
                               final Optional<HttpsCertificate> certificate,
                               final MocoMonitor monitor, final MocoConfig... configs) {
        super(port, monitor, configs);
        this.certificate = certificate;
    }

    public boolean isSecure() {
        return certificate.isPresent();
    }

    public Optional<HttpsCertificate> getCertificate() {
        return certificate;
    }

    public HttpServer mergeHttpServer(final ActualHttpServer thatServer) {
        ActualHttpServer newServer = newBaseServer(newServerCertificate(thatServer.certificate));
        newServer.addSettings(this.getSettings());
        newServer.addSettings(thatServer.getSettings());

        newServer.anySetting(configuredMatcher(), configured(this.handler));
        newServer.anySetting(thatServer.configuredMatcher(), thatServer.configured(thatServer.handler));

        newServer.addEvents(this.eventTriggers);
        newServer.addEvents(thatServer.eventTriggers);

        return newServer;
    }

    private Optional<HttpsCertificate> newServerCertificate(final Optional<HttpsCertificate> certificate) {
        if (this.isSecure()) {
            return this.certificate;
        }

        if (certificate.isPresent()) {
            return certificate;
        }

        return absent();
    }

    private ActualHttpServer newBaseServer(final Optional<HttpsCertificate> certificate) {
        if (certificate.isPresent()) {
            return createHttpsLogServer(getPort(), certificate.get());
        }

        return createLogServer(getPort());
    }

    public static ActualHttpServer createHttpServerWithMonitor(final Optional<Integer> port,
                                                               final MocoMonitor monitor,
                                                               final MocoConfig... configs) {
        return new ActualHttpServer(port, Optional.<HttpsCertificate>absent(), monitor, configs);
    }

    public static ActualHttpServer createLogServer(final Optional<Integer> port, final MocoConfig... configs) {
        return createHttpServerWithMonitor(port,
                new Slf4jMonitor(new HttpRequestDumper(), new HttpResponseDumper()), configs);
    }

    public static ActualHttpServer createQuietServer(final Optional<Integer> port, final MocoConfig... configs) {
        return createHttpServerWithMonitor(port, new QuietMonitor(), configs);
    }

    public static ActualHttpServer createHttpsServerWithMonitor(final Optional<Integer> port,
                                                                final HttpsCertificate certificate,
                                                                final MocoMonitor monitor,
                                                                final MocoConfig... configs) {
        return new ActualHttpServer(port, of(certificate), monitor, configs);
    }

    public static ActualHttpServer createHttpsLogServer(final Optional<Integer> port,
                                                        final HttpsCertificate certificate,
                                                        final MocoConfig... configs) {
        return createHttpsServerWithMonitor(port, certificate,
                new Slf4jMonitor(new HttpRequestDumper(), new HttpResponseDumper()), configs);
    }

    public static ActualHttpServer createHttpsQuietServer(final Optional<Integer> port,
                                                          final HttpsCertificate certificate,
                                                          final MocoConfig... configs) {
        return ActualHttpServer.createHttpsServerWithMonitor(port, certificate, new QuietMonitor(), configs);
    }

    @Override
    protected HttpSetting newSetting(final RequestMatcher matcher) {
        return new HttpSetting(matcher);
    }

    @Override
    public void resource(final String name, final Map<String, ? extends ResponseHandler> getHandlers) {
        checkNotNull(name, "Resource name should not be null");
        checkNotNull(getHandlers, "Get handlers should not be null");

        for (Map.Entry<String, ? extends ResponseHandler> entry : getHandlers.entrySet()) {
            this.get(by(uri(join(resourceRoot(name), entry.getKey())))).response(entry.getValue());
        }

        FluentIterable<? extends ResponseHandler> handlers = from(getHandlers.values());
        if (handlers.allMatch(isJsonHandlers())) {
            ImmutableList<Object> objects = handlers.transform(toJsonHandler()).transform(toPojo()).toList();
            this.get(by(uri(resourceRoot(name)))).response(Moco.toJson(objects));
        }
    }

    private Function<JsonResponseHandler, Object> toPojo() {
        return new Function<JsonResponseHandler, Object>() {
            @Override
            public Object apply(final JsonResponseHandler handler) {
                return handler.getPojo();
            }
        };
    }

    private Function<ResponseHandler, JsonResponseHandler> toJsonHandler() {
        return new Function<ResponseHandler, JsonResponseHandler>() {
            @Override
            public JsonResponseHandler apply(final ResponseHandler handler) {
                return JsonResponseHandler.class.cast(handler);
            }
        };
    }

    private Predicate<ResponseHandler> isJsonHandlers() {
        return new Predicate<ResponseHandler>() {
            @Override
            public boolean apply(final ResponseHandler handler) {
                return handler instanceof JsonResponseHandler;
            }
        };
    }
}
