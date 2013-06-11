package com.github.dreamhead.moco.dumper;

import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.util.internal.StringUtil;

public class HttpResponseDumper extends HttpMessageBaseDumper<HttpResponse> {
    public String dump(HttpResponse response) {
        StringBuilder buf = new StringBuilder();
        appendResponseProtocolLine(response, buf);
        buf.append(StringUtil.NEWLINE);
        headerJoiner.appendTo(buf, response.getHeaders());
        appendContent(response, buf);

        return buf.toString();
    }

    private void appendResponseProtocolLine(HttpResponse response, StringBuilder buf) {
        buf.append(response.getProtocolVersion().getText());
        buf.append(' ');
        buf.append(response.getStatus().toString());
    }
}
