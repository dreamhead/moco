package com.github.dreamhead.moco;

import io.netty.handler.codec.http.HttpRequest;

public interface RequestExtractor<T> {
    T extract(HttpRequest request);
}
