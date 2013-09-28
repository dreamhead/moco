package com.github.dreamhead.moco;

import com.google.common.base.Optional;
import io.netty.handler.codec.http.FullHttpRequest;

public interface RequestExtractor<T> {
    Optional<T> extract(FullHttpRequest request);
}
