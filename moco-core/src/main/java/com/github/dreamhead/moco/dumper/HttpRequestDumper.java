package com.github.dreamhead.moco.dumper;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.Request;
import com.google.common.base.Joiner;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.util.internal.StringUtil;

public class HttpRequestDumper implements Dumper<Request> {
    private final Joiner.MapJoiner headerJoiner = Joiner.on(StringUtil.NEWLINE).withKeyValueSeparator(": ");

    @Override
    public String dump(Request request) {
        HttpRequest httpRequest = (HttpRequest)request;
        StringBuilder buf = new StringBuilder();
        appendRequestProtocolLine(httpRequest, buf);
        buf.append(StringUtil.NEWLINE);
        headerJoiner.appendTo(buf, httpRequest.getHeaders());

        long contentLength = getContentLength(httpRequest, -1);
        if (contentLength > 0) {
            buf.append(StringUtil.NEWLINE);
            buf.append(StringUtil.NEWLINE);
            buf.append(request.getContent());
        }

        return buf.toString();
    }

    private void appendRequestProtocolLine(HttpRequest request, StringBuilder buf) {
        buf.append(request.getMethod());
        buf.append(' ');
        buf.append(request.getUri());
        buf.append(' ');
        buf.append(request.getVersion().text());
    }

    private long getContentLength(HttpRequest request, long defaultValue) {
        String contengLengthHeader = request.getHeaders().get(HttpHeaders.Names.CONTENT_LENGTH);
        if (contengLengthHeader != null) {
            try {
                return Long.parseLong(contengLengthHeader);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }

        return defaultValue;
    }
}
