package com.github.dreamhead.moco;

import com.github.dreamhead.moco.sse.SseEvent;

public interface MutableHttpResponse extends HttpResponse, MutableResponse {
    void setVersion(HttpProtocolVersion version);

    void setStatus(int status);

    void addHeader(String name, Object value);

    void setSseEvents(Iterable<SseEvent> events);
}
