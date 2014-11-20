package com.github.dreamhead.moco.dumper;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.Request;
import com.google.common.base.Joiner;
import io.netty.util.internal.StringUtil;

import static com.github.dreamhead.moco.dumper.HttpDumpers.appendContent;

public class HttpRequestDumper implements Dumper<Request> {
    private final Joiner.MapJoiner headerJoiner = Joiner.on(StringUtil.NEWLINE).withKeyValueSeparator(": ");

    @Override
    public String dump(final Request request) {
        HttpRequest httpRequest = (HttpRequest)request;
        StringBuilder buf = new StringBuilder();
        appendRequestProtocolLine(httpRequest, buf);
        buf.append(StringUtil.NEWLINE);
        headerJoiner.appendTo(buf, httpRequest.getHeaders());

        appendContent(httpRequest, buf);
        return buf.toString();
    }

    private void appendRequestProtocolLine(HttpRequest request, StringBuilder buf) {
        buf.append(request.getMethod());
        buf.append(' ');
        buf.append(request.getUri());
        buf.append(' ');
        buf.append(request.getVersion().text());
    }
}
