package com.github.dreamhead.moco.handler;

import io.netty.handler.codec.http.HttpResponse;

public class ContentTypeDetector {
    public boolean hasContentType(HttpResponse response) {
        return hasHeader(response, "Content-Type");
    }

    public boolean hasHeader(HttpResponse response, String headerName) {
        return response.headers().contains(headerName);
    }
}
