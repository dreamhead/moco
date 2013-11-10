package com.github.dreamhead.moco.handler;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponse;

public class HeaderDetector {
    public boolean hasContentType(HttpResponse response) {
        return hasHeader(response, HttpHeaders.Names.CONTENT_TYPE);
    }

    public boolean hasHeader(HttpResponse response, String headerName) {
        return response.headers().contains(headerName);
    }
}
