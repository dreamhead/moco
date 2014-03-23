package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.HttpsServer;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.MocoMonitor;
import com.github.dreamhead.moco.monitor.QuietMonitor;
import com.github.dreamhead.moco.monitor.Slf4jMonitor;
import com.google.common.base.Optional;

public class ActualHttpsServer extends ActualHttpServer implements HttpsServer {
    private final HttpsCertificate certificate;

    protected ActualHttpsServer(Optional<Integer> port, HttpsCertificate certificate, MocoMonitor monitor, MocoConfig... configs) {
        super(port, monitor, configs);
        this.certificate = certificate;
    }

    public boolean isSecure() {
        return true;
    }

    public HttpsCertificate getCertificate() {
        return certificate;
    }

    public static ActualHttpsServer createHttpServerWithMonitor(Optional<Integer> port, HttpsCertificate certificate, MocoMonitor monitor, MocoConfig... configs) {
        return new ActualHttpsServer(port, certificate, monitor, configs);
    }

    public static ActualHttpsServer createLogServer(Optional<Integer> port, HttpsCertificate certificate, MocoConfig... configs) {
        return createHttpServerWithMonitor(port, certificate, new Slf4jMonitor(), configs);
    }

    public static ActualHttpsServer createQuietServer(Optional<Integer> port, HttpsCertificate certificate, MocoConfig... configs) {
        return ActualHttpsServer.createHttpServerWithMonitor(port, certificate, new QuietMonitor(), configs);
    }
}
