package com.github.dreamhead.moco;

import org.jboss.netty.handler.codec.http.HttpRequest;

public interface RequestExtractor {
    String extract(HttpRequest request);
}
