package com.github.moco.matcher;

import com.github.moco.RequestMatcher;
import org.jboss.netty.handler.codec.http.HttpRequest;

public class UriRequestMatcher implements RequestMatcher {
    private final String uri;

    public UriRequestMatcher(final String uri) {
        this.uri = uri;
    }

    @Override
    public boolean match(HttpRequest request) {
        return request.getUri().equals(uri);
    }
}
