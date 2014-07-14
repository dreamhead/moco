package com.github.dreamhead.moco;

import com.google.common.collect.ImmutableMap;

public interface MutableHttpResponse extends HttpResponse {
    void setVersion(HttpProtocolVersion version);

    void setHeaders(ImmutableMap<String, String> headers);

    void setStatus(int status);

    void setContent(String content);
}
