package com.github.dreamhead.moco.model;

import com.github.dreamhead.moco.HttpResponse;
import com.google.common.base.Strings;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.FullHttpResponse;

import java.nio.charset.Charset;
import java.util.Map;

public class DumpHttpResponse extends DumpMessage implements HttpResponse {
    private int statusCode;

    public int getStatusCode() {
        return statusCode;
    }

    public static DumpHttpResponse newResponse(FullHttpResponse response) {
        DumpHttpResponse httpResponse = new DumpHttpResponse();
        httpResponse.statusCode = response.getStatus().code();
        httpResponse.version = response.getProtocolVersion().text();
        for (Map.Entry<String, String> entry : response.headers()) {
            httpResponse.headers.put(entry.getKey(), entry.getValue());
        }
        setContent(response, httpResponse);
        return httpResponse;
    }

    private static void setContent(FullHttpMessage message, DumpMessage dumpedDumpMessage) {
        String text = message.content().toString(Charset.defaultCharset());
        if (!Strings.isNullOrEmpty(text)) {
            dumpedDumpMessage.content = text;
        }
    }
}
