package com.github.dreamhead.moco.model;

import com.google.common.base.Strings;
import io.netty.handler.codec.http.*;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

public class MessageFactory {
    private static void setContent(FullHttpMessage message, Message dumpedMessage) {
        String text = message.content().toString(Charset.defaultCharset());
        if (!Strings.isNullOrEmpty(text)) {
            dumpedMessage.setContent(text);
        }
    }

    public static DefaultHttpRequest createRequest(FullHttpRequest request) {
        DefaultHttpRequest dumpedRequest = new DefaultHttpRequest();
        dumpedRequest.setVersion(request.getProtocolVersion().text());
        dumpedRequest.setUri(request.getUri());
        setContent(request, dumpedRequest);
        dumpedRequest.setMethod(request.getMethod().name());

        QueryStringDecoder decoder = new QueryStringDecoder(request.getUri());
        for (Map.Entry<String, List<String>> entry : decoder.parameters().entrySet()) {
            dumpedRequest.addQuery(entry.getKey(), entry.getValue().get(0));
        }

        for (Map.Entry<String, String> entry : request.headers()) {
            dumpedRequest.addHeader(entry.getKey(), entry.getValue());
        }

        return dumpedRequest;
    }

    public static Response createResponse(FullHttpResponse response) {
        Response dumpedResponse = new Response();
        dumpedResponse.setStatusCode(response.getStatus().code());
        dumpedResponse.setVersion(response.getProtocolVersion().text());
        for (Map.Entry<String, String> entry : response.headers()) {
            dumpedResponse.addHeader(entry.getKey(), entry.getValue());
        }
        setContent(response, dumpedResponse);
        return dumpedResponse;
    }
}
