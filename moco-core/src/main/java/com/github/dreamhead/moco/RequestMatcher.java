package com.github.dreamhead.moco;

import io.netty.handler.codec.http.HttpRequest;

public interface RequestMatcher extends ConfigApplier<RequestMatcher> {
    boolean match(final HttpRequest request);
}
