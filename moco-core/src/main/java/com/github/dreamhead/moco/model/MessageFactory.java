package com.github.dreamhead.moco.model;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.HttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.util.Map;

public class MessageFactory {
    public static HttpRequest createRequest(FullHttpRequest request) {
        return new LazyHttpRequest(request);
    }

    public static HttpResponse createResponse(FullHttpResponse response) {
        return DumpHttpResponse.newResponse(response);
    }

    public static void writeResponse(FullHttpResponse response, HttpResponse targetHttpResponse) {
        response.setProtocolVersion(HttpVersion.valueOf(targetHttpResponse.getVersion()));
        response.setStatus(HttpResponseStatus.valueOf(targetHttpResponse.getStatusCode()));
        for (Map.Entry<String, String> entry : targetHttpResponse.getHeaders().entrySet()) {
            response.headers().add(entry.getKey(), entry.getValue());
        }

        response.content().writeBytes(targetHttpResponse.getContent().getBytes());
    }
}
