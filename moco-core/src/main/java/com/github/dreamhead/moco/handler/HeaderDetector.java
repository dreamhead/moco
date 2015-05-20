package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.HttpResponse;
import com.google.common.net.HttpHeaders;

public class HeaderDetector {
    public boolean hasContentType(final HttpResponse httpResponse) {
        return hasHeader(httpResponse, HttpHeaders.CONTENT_TYPE);
    }

    public boolean hasHeader(final HttpResponse httpResponse, final String name) {
        return httpResponse.getHeaders().containsKey(name);
    }
}
