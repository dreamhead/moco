package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.HttpResponse;
import com.google.common.net.HttpHeaders;

public class HeaderDetector {
    public boolean hasContentType(HttpResponse httpResponse) {
        return hasHeader(httpResponse, HttpHeaders.CONTENT_TYPE);
    }

    public boolean hasHeader(HttpResponse httpResponse, String name) {
        return httpResponse.getHeaders().containsKey(name);
    }
}
