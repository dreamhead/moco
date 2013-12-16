package com.github.dreamhead.moco.model;

import com.github.dreamhead.moco.HttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.util.Map;

public class MessageFactory {
    public static void writeResponse(FullHttpResponse response, HttpResponse targetHttpResponse) {
        response.setProtocolVersion(HttpVersion.valueOf(targetHttpResponse.getVersion()));
        response.setStatus(HttpResponseStatus.valueOf(targetHttpResponse.getStatus()));
        for (Map.Entry<String, String> entry : targetHttpResponse.getHeaders().entrySet()) {
            response.headers().add(entry.getKey(), entry.getValue());
        }

        response.content().writeBytes(targetHttpResponse.getContent().getBytes());
    }
}
