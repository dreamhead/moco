package com.github.dreamhead.moco.dumper;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.Request;
import io.netty.util.internal.StringUtil;

import static com.github.dreamhead.moco.dumper.HttpDumpers.asContent;
import static com.github.dreamhead.moco.dumper.HttpDumpers.asHeaders;

public final class HttpRequestDumper implements Dumper<Request> {
    @Override
    public String dump(final Request request) {
        HttpRequest httpRequest = (HttpRequest) request;
        StringBuilder buf = new StringBuilder();
        buf.append(requestProtocolLine(httpRequest))
                .append(StringUtil.NEWLINE)
                .append(asHeaders(httpRequest))
                .append(asContent(httpRequest));
        return buf.toString();
    }

    private String requestProtocolLine(final HttpRequest request) {
        return request.getMethod().name() + ' ' + request.getUri() + ' ' + request.getVersion().text();
    }
}
