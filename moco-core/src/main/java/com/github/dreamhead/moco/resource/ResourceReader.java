package com.github.dreamhead.moco.resource;

import io.netty.handler.codec.http.FullHttpRequest;

public interface ResourceReader {
    byte[] readFor(FullHttpRequest request);
}
