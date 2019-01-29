package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.handler.failover.Failover;
import com.github.dreamhead.moco.handler.proxy.ProxyConfig;
import com.google.common.base.Optional;

import static com.github.dreamhead.moco.Moco.from;
import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;

public final class ProxyBatchResponseHandler extends AbstractProxyResponseHandler {
    private final ProxyConfig proxyConfig;

    public ProxyBatchResponseHandler(final ProxyConfig proxyConfig,
                                     final Failover failover) {
        super(failover);
        this.proxyConfig = proxyConfig;
    }

    @Override
    protected Optional<String> doRemoteUrl(final HttpRequest request) {
        String uri = request.getUri();
        if (!proxyConfig.canAccessedBy(uri)) {
            return absent();
        }

        return of(proxyConfig.remoteUrl(uri));
    }

    @Override
    @SuppressWarnings("unchecked")
    public ResponseHandler apply(final MocoConfig config) {
        if (config.isFor(MocoConfig.URI_ID)) {
            String newLocalBase = (String) config.apply(proxyConfig.localBase());
            return new ProxyBatchResponseHandler(from(newLocalBase).to(proxyConfig.remoteBase()), failover());
        }

        return this;
    }
}
