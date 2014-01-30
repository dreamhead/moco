package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.handler.failover.Failover;
import com.google.common.base.Optional;

import java.net.URL;

import static com.google.common.base.Optional.of;

public class ProxyResponseHandler extends AbstractProxyResponseHandler implements ResponseHandler {
    private final URL url;

    public ProxyResponseHandler(URL url, Failover failover) {
        super(failover);
        this.url = url;
    }

    @Override
    protected Optional<String> remoteUrl(String uri) {
        return of(this.url.toString());
    }
}
