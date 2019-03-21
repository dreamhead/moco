package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.HttpsCertificate;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.MocoMonitor;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.dumper.HttpRequestDumper;
import com.github.dreamhead.moco.dumper.HttpResponseDumper;
import com.github.dreamhead.moco.monitor.QuietMonitor;
import com.github.dreamhead.moco.monitor.Slf4jMonitor;
import com.github.dreamhead.moco.monitor.ThreadSafeMonitor;
import com.github.dreamhead.moco.setting.HttpSetting;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;

public class ActualHttpServer extends HttpConfiguration<ActualHttpServer> {
    private final HttpsCertificate certificate;

    protected ActualHttpServer(final Optional<Integer> port,
                               final HttpsCertificate certificate,
                               final MocoMonitor monitor, final MocoConfig... configs) {
        super(port, monitor, configs);
        this.certificate = certificate;
    }

    public final boolean isSecure() {
        return certificate != null;
    }

    public Optional<SslHandler> sslHandler() {
        if (this.certificate != null) {
            return Optional.of(asSslHandler(certificate));
        }

        return absent();
    }

    private SslHandler asSslHandler(final HttpsCertificate certificate) {
        SSLEngine sslEngine = certificate.createSSLEngine();
        sslEngine.setUseClientMode(false);
        return new SslHandler(sslEngine);
    }

    protected final ActualHttpServer createMergeServer(final ActualHttpServer thatServer) {
        return newBaseServer(mergePort(this, thatServer), mergedCertificate(this.certificate, thatServer.certificate));
    }

    private Optional<Integer> mergePort(final ActualHttpServer thisServer, final ActualHttpServer thatServer) {
        return thisServer.getPort().or(thatServer.getPort());
    }


    private HttpsCertificate mergedCertificate(final HttpsCertificate one, final HttpsCertificate other) {
        if (one != null) {
            return one;
        }

        return other;
    }

    private ActualHttpServer newBaseServer(final Optional<Integer> port, final HttpsCertificate certificate) {
        if (certificate != null) {
            return createHttpsLogServer(port, certificate);
        }

        return createLogServer(port);
    }

    public static ActualHttpServer createHttpServerWithMonitor(final Optional<Integer> port,
                                                               final MocoMonitor monitor,
                                                               final MocoConfig... configs) {
        return new ActualHttpServer(port, null, new ThreadSafeMonitor(monitor), configs);
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
        return new ActualHttpServer(port, certificate, monitor, configs);
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
    protected final HttpSetting newSetting(final RequestMatcher matcher) {
        return new HttpSetting(matcher);
    }
}
