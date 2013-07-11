package com.github.dreamhead.moco;

import org.jboss.netty.handler.codec.http.HttpRequest;

public interface RequestMatcher extends ConfigApplier<RequestMatcher> {
    boolean match(final HttpRequest request);
}
