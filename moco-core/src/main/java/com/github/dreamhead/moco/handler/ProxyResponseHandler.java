package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.handler.failover.Failover;
import com.google.common.base.Optional;

import java.net.URL;

import static com.google.common.base.Optional.of;

public class ProxyResponseHandler extends AbstractProxyResponseHandler implements ResponseHandler {
    private final URL url;

    public ProxyResponseHandler(final URL url, final Failover failover) {
        super(failover);
        this.url = url;
    }

    @Override
    protected Optional<String> remoteUrl(final String uri) {
        return of(this.url.toString());
    }
}
