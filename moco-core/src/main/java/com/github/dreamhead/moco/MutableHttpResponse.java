package com.github.dreamhead.moco;

public interface MutableHttpResponse extends HttpResponse, MutableResponse {
    void setVersion(final HttpProtocolVersion version);

    void setStatus(final int status);

    void addHeader(final String name, final Object value);

    void removeHeader(final String name);
}
