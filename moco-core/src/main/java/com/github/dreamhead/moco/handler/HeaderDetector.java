package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.HttpResponse;
import com.google.common.net.HttpHeaders;

public class HeaderDetector {
    public final boolean hasContentType(final HttpResponse httpResponse) {
        return hasHeader(httpResponse, HttpHeaders.CONTENT_TYPE);
    }

    public final boolean hasHeader(final HttpResponse httpResponse, final String name) {
        return httpResponse.getHeaders().entrySet().stream()
                .anyMatch(input -> com.github.dreamhead.moco.util.HttpHeaders.isSameHeaderName(input.getKey(), name));
    }
}
