package com.github.dreamhead.moco.model;

import com.google.common.base.Strings;
import org.jboss.netty.handler.codec.http.HttpMessage;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

public class MessageFactory {
    private static void setContent(HttpMessage message, Message dumpedMessage) {
        String content = message.getContent().toString(Charset.defaultCharset());
        if (!Strings.isNullOrEmpty(content)) {
            dumpedMessage.setContent(content);
        }
    }

    public static Request createRequest(HttpRequest request) {
        Request dumpedRequest = new Request();
        dumpedRequest.setVersion(request.getProtocolVersion().getText());
        setContent(request, dumpedRequest);
        dumpedRequest.setMethod(request.getMethod().getName());

        QueryStringDecoder decoder = new QueryStringDecoder(request.getUri());
        for (Map.Entry<String, List<String>> entry : decoder.getParameters().entrySet()) {
            dumpedRequest.addQuery(entry.getKey(), entry.getValue().get(0));
        }

        for (Map.Entry<String, String> entry : request.getHeaders()) {
            dumpedRequest.addHeader(entry.getKey(), entry.getValue());
        }

        return dumpedRequest;
    }

    public static Response createResponse(HttpResponse response) {
        Response dumpedResponse = new Response();
        dumpedResponse.setStatusCode(response.getStatus().getCode());
        dumpedResponse.setVersion(response.getProtocolVersion().getText());
        for (Map.Entry<String, String> entry : response.getHeaders()) {
            dumpedResponse.addHeader(entry.getKey(), entry.getValue());
        }
        setContent(response, dumpedResponse);
        return dumpedResponse;
    }
}
