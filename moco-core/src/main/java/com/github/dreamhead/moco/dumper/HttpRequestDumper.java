package com.github.dreamhead.moco.dumper;

import com.github.dreamhead.moco.HttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.util.internal.StringUtil;

public class HttpRequestDumper extends HttpMessageBaseDumper<HttpRequest> {
    public String dump(HttpRequest request) {
        StringBuilder buf = new StringBuilder();
        appendRequestProtocolLine(request, buf);
        buf.append(StringUtil.NEWLINE);
        headerJoiner.appendTo(buf, request.getHeaders());

        long contentLength = getContentLength(request, -1);
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
