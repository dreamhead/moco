package com.github.dreamhead.moco.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.dreamhead.moco.HttpResponse;
import com.google.common.base.Strings;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.FullHttpResponse;

import java.nio.charset.Charset;
import java.util.Map;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class DumpHttpResponse extends DumpMessage implements HttpResponse {
    private int statusCode;

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public static DumpHttpResponse newResponse(FullHttpResponse response) {
        DumpHttpResponse httpResponse = new DumpHttpResponse();
        httpResponse.setStatusCode(response.getStatus().code());
        httpResponse.setVersion(response.getProtocolVersion().text());
        for (Map.Entry<String, String> entry : response.headers()) {
            httpResponse.addHeader(entry.getKey(), entry.getValue());
        }
        setContent(response, httpResponse);
        return httpResponse;
    }

    private static void setContent(FullHttpMessage message, DumpMessage dumpedDumpMessage) {
        String text = message.content().toString(Charset.defaultCharset());
        if (!Strings.isNullOrEmpty(text)) {
            dumpedDumpMessage.setContent(text);
        }
    }
}
