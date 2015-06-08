package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.HttpServer;
import com.github.dreamhead.moco.HttpsCertificate;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.MocoMonitor;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.dumper.HttpRequestDumper;
import com.github.dreamhead.moco.dumper.HttpResponseDumper;
import com.github.dreamhead.moco.monitor.QuietMonitor;
import com.github.dreamhead.moco.monitor.Slf4jMonitor;
import com.github.dreamhead.moco.setting.HttpSetting;
import com.google.common.base.Optional;

import static com.github.dreamhead.moco.util.Configs.configItem;
import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;

public class ActualHttpServer extends HttpConfiguration {
    protected final Optional<HttpsCertificate> certificate;

    protected ActualHttpServer(final Optional<Integer> port, final Optional<HttpsCertificate> certificate, final MocoMonitor monitor, final MocoConfig... configs) {
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

        newServer.anySetting(configItem(this.matcher, this.configs), configItem(this.handler, this.configs));
        newServer.anySetting(configItem(thatServer.matcher, thatServer.configs), configItem(thatServer.handler, thatServer.configs));

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

    public static ActualHttpServer createHttpServerWithMonitor(final Optional<Integer> port, final MocoMonitor monitor, final MocoConfig... configs) {
        return new ActualHttpServer(port, Optional.<HttpsCertificate>absent(), monitor, configs);
    }

    public static ActualHttpServer createLogServer(final Optional<Integer> port, final MocoConfig... configs) {
        return createHttpServerWithMonitor(port, new Slf4jMonitor(new HttpRequestDumper(), new HttpResponseDumper()), configs);
    }

    public static ActualHttpServer createQuietServer(final Optional<Integer> port, final MocoConfig... configs) {
        return createHttpServerWithMonitor(port, new QuietMonitor(), configs);
    }

    public static ActualHttpServer createHttpsServerWithMonitor(final Optional<Integer> port, final HttpsCertificate certificate, MocoMonitor monitor, MocoConfig... configs) {
        return new ActualHttpServer(port, of(certificate), monitor, configs);
    }

    public static ActualHttpServer createHttpsLogServer(final Optional<Integer> port, final HttpsCertificate certificate, final MocoConfig... configs) {
        return createHttpsServerWithMonitor(port, certificate, new Slf4jMonitor(new HttpRequestDumper(), new HttpResponseDumper()), configs);
    }

    public static ActualHttpServer createHttpsQuietServer(final Optional<Integer> port, final HttpsCertificate certificate, final MocoConfig... configs) {
        return ActualHttpServer.createHttpsServerWithMonitor(port, certificate, new QuietMonitor(), configs);
    }

    @Override
    protected HttpSetting newSetting(final RequestMatcher matcher) {
        return new HttpSetting(matcher);
    }
}
