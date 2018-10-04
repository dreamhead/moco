package com.github.dreamhead.moco;

public interface MutableHttpResponse extends HttpResponse, MutableResponse {
    void setVersion(HttpProtocolVersion version);

    void setStatus(int status);

    void addHeader(String name, Object value);
}
