package com.github.dreamhead.moco.handler;

import io.netty.handler.codec.http.FullHttpRequest;

public interface ResponseStatusEvaluator {
    boolean shouldReturnSuccessfulStatus(FullHttpRequest request);
}
