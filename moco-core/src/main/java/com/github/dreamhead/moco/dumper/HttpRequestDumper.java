package com.github.dreamhead.moco.dumper;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.Request;
import com.google.common.collect.ImmutableMap;
import io.netty.util.internal.StringUtil;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.dreamhead.moco.dumper.HttpDumpers.asContent;
import static com.github.dreamhead.moco.dumper.HttpDumpers.asHeaders;
import static java.util.stream.Collectors.joining;

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
        final String queryResult = queries.entrySet().stream()
                .flatMap(entry -> toValueStream(entry.getKey(), entry.getValue()))
                .collect(joining("&"));
        buf.append(queryResult);
        return buf.toString();
    }

    private Stream<String> toValueStream(final String key, final String[] value) {
        return Arrays.stream(value)
                .map(result -> key + "=" + result);
    }
}
