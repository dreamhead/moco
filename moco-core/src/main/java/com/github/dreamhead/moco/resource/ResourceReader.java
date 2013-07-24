package com.github.dreamhead.moco.resource;

import io.netty.handler.codec.http.HttpRequest;

public interface ResourceReader {
    byte[] readFor(HttpRequest request);
}
