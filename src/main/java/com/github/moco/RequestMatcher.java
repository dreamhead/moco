package com.github.moco;

import org.jboss.netty.handler.codec.http.HttpRequest;

public interface RequestMatcher {
    boolean match(HttpRequest request);
}
