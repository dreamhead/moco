package com.github.dreamhead.moco.model;

import com.github.dreamhead.moco.HttpRequest;
import com.google.common.base.Strings;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

import java.nio.charset.Charset;
import java.util.Map;

public class MessageFactory {
    private static void setContent(FullHttpMessage message, Message dumpedMessage) {
        String text = message.content().toString(Charset.defaultCharset());
        if (!Strings.isNullOrEmpty(text)) {
            dumpedMessage.setContent(text);
        }
    }

    public static HttpRequest createRequest(FullHttpRequest request) {
        return new LazyHttpRequest(request);
    }

    public static HttpResponse createResponse(FullHttpResponse response) {
        HttpResponse dumpedResponse = new HttpResponse();
        dumpedResponse.setStatusCode(response.getStatus().code());
        dumpedResponse.setVersion(response.getProtocolVersion().text());
        for (Map.Entry<String, String> entry : response.headers()) {
            dumpedResponse.addHeader(entry.getKey(), entry.getValue());
        }
        setContent(response, dumpedResponse);
        return dumpedResponse;
    }
}
