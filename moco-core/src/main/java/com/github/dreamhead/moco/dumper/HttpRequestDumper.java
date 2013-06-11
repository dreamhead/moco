package com.github.dreamhead.moco.dumper;

import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.util.internal.StringUtil;

public class HttpRequestDumper extends HttpMessageBaseDumper<HttpRequest> {
    public String dump(HttpRequest request) {
        StringBuilder buf = new StringBuilder();
        appendRequestProtocolLine(request, buf);
        buf.append(StringUtil.NEWLINE);
        headerJoiner.appendTo(buf, request.getHeaders());
        appendContent(request, buf);

        return buf.toString();
    }

    private void appendRequestProtocolLine(HttpRequest request, StringBuilder buf) {
        buf.append(request.getMethod().toString());
        buf.append(' ');
        buf.append(request.getUri());
        buf.append(' ');
        buf.append(request.getProtocolVersion().getText());
    }
}
