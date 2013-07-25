package com.github.dreamhead.moco;

import io.netty.handler.codec.http.FullHttpRequest;

public interface RequestMatcher extends ConfigApplier<RequestMatcher> {
    boolean match(final FullHttpRequest request);
}
