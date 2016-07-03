package com.github.dreamhead.moco.internal;

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

import static com.google.common.base.Optional.of;

public class ActualHttpServer extends HttpConfiguration<ActualHttpServer> {
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

    protected ActualHttpServer createMergeServer(final ActualHttpServer thatServer) {
        return newBaseServer(this.getPort().or(thatServer.getPort()), this.certificate.or(thatServer.certificate));
    }

    private ActualHttpServer newBaseServer(final Optional<Integer> port, final Optional<HttpsCertificate> certificate) {
        if (certificate.isPresent()) {
            return createHttpsLogServer(port, certificate.get());
        }

        return createLogServer(port);
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
}
