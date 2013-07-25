package com.github.dreamhead.moco.dumper;

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.util.internal.StringUtil;

public class HttpResponseDumper extends HttpMessageBaseDumper<FullHttpResponse> {
    @Override
    public String dump(FullHttpResponse response) {
        StringBuilder buf = new StringBuilder();
        appendResponseProtocolLine(response, buf);
        buf.append(StringUtil.NEWLINE);
        headerJoiner.appendTo(buf, response.headers());
        appendContent(response, buf);

        return buf.toString();
    }

    private void appendResponseProtocolLine(HttpResponse response, StringBuilder buf) {
        buf.append(response.getProtocolVersion().text());
        buf.append(' ');
        buf.append(response.getStatus().toString());
    }
}
