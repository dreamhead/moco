package com.github.dreamhead.moco;

public interface MutableHttpResponse extends HttpResponse {
    void setVersion(HttpProtocolVersion version);

    void setStatus(int status);

    void setContent(String content);

    void addHeader(String name, Object value);

    void removeHeader(String name);
}
