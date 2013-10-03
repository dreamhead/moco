package com.github.dreamhead.moco.model;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.HttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public class MessageFactory {
    public static HttpRequest createRequest(FullHttpRequest request) {
        return new LazyHttpRequest(request);
    }

    public static HttpResponse createResponse(FullHttpResponse response) {
        return DumpHttpResponse.newResponse(response);
    }
}
