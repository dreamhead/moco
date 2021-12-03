package com.github.dreamhead.moco.dumper;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.Request;
import com.google.common.collect.ImmutableMap;
import io.netty.util.internal.StringUtil;

import java.util.Map;

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
        StringBuilder buf = new StringBuilder();
        buf.append(request.getMethod().name())
                .append(' ')
                .append(request.getUri())
                .append(asQuery(request.getQueries()))
                .append(' ')
                .append(request.getVersion().text());
        return buf.toString();
    }

    private String asQuery(final ImmutableMap<String, String[]> queries) {
        if (queries.isEmpty()) {
            return "";
        }

        final StringBuilder buf = new StringBuilder();
        buf.append('?');
        for (Map.Entry<String, String[]> entry : queries.entrySet()) {
            final String key = entry.getKey();
            for (String value : entry.getValue()) {
                buf.append(key).append('=').append(value);
            }
        }

        return buf.toString();
    }
}
