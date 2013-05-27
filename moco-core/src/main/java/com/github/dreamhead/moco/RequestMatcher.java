package com.github.dreamhead.moco;

import org.jboss.netty.handler.codec.http.HttpRequest;

public interface RequestMatcher {
    boolean match(HttpRequest request);

    RequestMatcher apply(final MocoConfig config);
}
