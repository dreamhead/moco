package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.handler.failover.Failover;
import com.github.dreamhead.moco.handler.proxy.ProxyConfig;
import com.google.common.base.Optional;

import java.net.MalformedURLException;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;

public class ProxyBatchResponseHandler extends AbstractProxyResponseHandler {
    private final ProxyConfig proxyConfig;

    public ProxyBatchResponseHandler(ProxyConfig proxyConfig, Failover failover) {
        super(failover);
        this.proxyConfig = proxyConfig;
    }

    @Override
    protected Optional<String> remoteUrl(String uri) throws MalformedURLException {
        if (!proxyConfig.canAccessedBy(uri)) {
            return absent();
        }

        return of(proxyConfig.remoteUrl(uri));
    }
}
