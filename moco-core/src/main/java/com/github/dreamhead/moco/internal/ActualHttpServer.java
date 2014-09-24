package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.*;
import com.github.dreamhead.moco.dumper.HttpRequestDumper;
import com.github.dreamhead.moco.dumper.HttpResponseDumper;
import com.github.dreamhead.moco.monitor.QuietMonitor;
import com.github.dreamhead.moco.monitor.Slf4jMonitor;
import com.github.dreamhead.moco.setting.HttpSetting;
import com.google.common.base.Optional;
import com.google.common.net.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;

import static com.github.dreamhead.moco.Moco.header;
import static com.github.dreamhead.moco.Moco.status;
import static com.github.dreamhead.moco.util.Configs.configItem;
import static com.github.dreamhead.moco.util.Preconditions.checkNotNullOrEmpty;
import static com.google.common.base.Optional.of;

public class ActualHttpServer extends HttpConfiguration {
    protected final Optional<HttpsCertificate> certificate;

    protected ActualHttpServer(Optional<Integer> port, Optional<HttpsCertificate> certificate, MocoMonitor monitor, MocoConfig... configs) {
        super(port, monitor, configs);
        this.certificate = certificate;
    }

    public boolean isSecure() {
        return certificate.isPresent();
    }

    public Optional<HttpsCertificate> getCertificate() {
        return certificate;
    }

    public HttpServer mergeHttpServer(ActualHttpServer thatServer) {
        ActualHttpServer newServer = newBaseServer();
        newServer.addSettings(this.getSettings());
        newServer.addSettings(thatServer.getSettings());

        newServer.anySetting(configItem(this.matcher, this.configs), configItem(this.handler, this.configs));
        newServer.anySetting(configItem(thatServer.matcher, thatServer.configs), configItem(thatServer.handler, thatServer.configs));

        newServer.addEvents(this.eventTriggers);
        newServer.addEvents(thatServer.eventTriggers);

        return newServer;
    }

    private ActualHttpServer newBaseServer() {
        if (isSecure()) {
            return createHttpsLogServer(getPort(), certificate.get());
        }

        return createLogServer(getPort());
    }

    public static ActualHttpServer createHttpServerWithMonitor(Optional<Integer> port, MocoMonitor monitor, MocoConfig... configs) {
        return new ActualHttpServer(port, Optional.<HttpsCertificate>absent(), monitor, configs);
    }

    public static ActualHttpServer createLogServer(Optional<Integer> port, MocoConfig... configs) {
        return createHttpServerWithMonitor(port, new Slf4jMonitor(new HttpRequestDumper(), new HttpResponseDumper()), configs);
    }

    public static ActualHttpServer createQuietServer(Optional<Integer> port, MocoConfig... configs) {
        return createHttpServerWithMonitor(port, new QuietMonitor(), configs);
    }

    public static ActualHttpServer createHttpsServerWithMonitor(Optional<Integer> port, HttpsCertificate certificate, MocoMonitor monitor, MocoConfig... configs) {
        return new ActualHttpServer(port, of(certificate), monitor, configs);
    }

    public static ActualHttpServer createHttpsLogServer(Optional<Integer> port, HttpsCertificate certificate, MocoConfig... configs) {
        return createHttpsServerWithMonitor(port, certificate, new Slf4jMonitor(new HttpRequestDumper(), new HttpResponseDumper()), configs);
    }

    public static ActualHttpServer createHttpsQuietServer(Optional<Integer> port, HttpsCertificate certificate, MocoConfig... configs) {
        return ActualHttpServer.createHttpsServerWithMonitor(port, certificate, new QuietMonitor(), configs);
    }

    @Override
    protected HttpResponseSetting self() {
        return this;
    }

    @Override
    public HttpResponseSetting redirectTo(String url) {
        return this.response(status(HttpResponseStatus.FOUND.code()), header(HttpHeaders.LOCATION, checkNotNullOrEmpty(url, "URL should not be null")));
    }

    @Override
    protected HttpResponseSetting onRequestAttached(final RequestMatcher matcher) {
        HttpSetting baseSetting = new HttpSetting(matcher);
        addSetting(baseSetting);
        return baseSetting;
    }

    @Override
    protected HttpSetting newSetting(RequestMatcher matcher) {
        return new HttpSetting(matcher);
    }
}
