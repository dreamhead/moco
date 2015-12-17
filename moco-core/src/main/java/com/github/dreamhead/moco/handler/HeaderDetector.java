package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.HttpResponse;
import com.google.common.net.HttpHeaders;

import static com.github.dreamhead.moco.util.HttpHeaders.isForHeaderName;
import static com.google.common.collect.FluentIterable.from;

public class HeaderDetector {
    public boolean hasContentType(final HttpResponse httpResponse) {
        return hasHeader(httpResponse, HttpHeaders.CONTENT_TYPE);
    }

    public boolean hasHeader(final HttpResponse httpResponse, final String name) {
        return from(httpResponse.getHeaders().entrySet()).anyMatch(isForHeaderName(name));
    }
}
