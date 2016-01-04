package com.github.dreamhead.moco.dumper;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.Request;
import com.google.common.base.Joiner;
import io.netty.util.internal.StringUtil;

import static com.github.dreamhead.moco.dumper.HttpDumpers.asContent;

public class HttpRequestDumper implements Dumper<Request> {
    private final Joiner.MapJoiner headerJoiner = Joiner.on(StringUtil.NEWLINE).withKeyValueSeparator(": ");

    @Override
    public String dump(final Request request) {
        HttpRequest httpRequest = (HttpRequest) request;
        StringBuilder buf = new StringBuilder();
        buf.append(requestProtocolLine(httpRequest))
                .append(StringUtil.NEWLINE)
                .append(headerJoiner.join(httpRequest.getHeaders()))
                .append(asContent(httpRequest));
        return buf.toString();
    }

    private String requestProtocolLine(final HttpRequest request) {
        return request.getMethod().name() + ' ' + request.getUri() + ' ' + request.getVersion().text();
    }
}
