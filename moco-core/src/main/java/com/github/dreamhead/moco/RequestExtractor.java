package com.github.dreamhead.moco;

import io.netty.handler.codec.http.FullHttpRequest;

public interface RequestExtractor<T> {
    T extract(FullHttpRequest request);
}
