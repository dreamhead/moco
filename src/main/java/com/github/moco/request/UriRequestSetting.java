package com.github.moco.request;

import com.github.moco.MocoServer;
import com.github.moco.request.BaseRequestSetting;
import org.jboss.netty.handler.codec.http.HttpRequest;

public class UriRequestSetting extends BaseRequestSetting {
    private String uri;

    public UriRequestSetting(MocoServer server, String uri) {
        super(server);
        this.uri = uri;
    }

    @Override
    public boolean isMatchAny() {
        return false;
    }

    @Override
    public boolean match(HttpRequest request) {
        return (request.getUri().equals(uri));
    }
}
