package com.github.dreamhead.moco.resource;

import org.jboss.netty.handler.codec.http.HttpRequest;

public interface ResourceReader {
    byte[] readFor(HttpRequest request);
}
