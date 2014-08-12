package com.github.dreamhead.moco;

public interface MutableHttpResponse extends HttpResponse {
    void setVersion(final HttpProtocolVersion version);

    void setStatus(final int status);

    void setContent(final String content);

    void addHeader(final String name, final Object value);

    void removeHeader(final String name);
}
