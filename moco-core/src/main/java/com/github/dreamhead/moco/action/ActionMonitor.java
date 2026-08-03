package com.github.dreamhead.moco.action;

import com.github.dreamhead.moco.HttpMethod;
import com.github.dreamhead.moco.HttpProtocolVersion;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.Response;
import com.github.dreamhead.moco.dumper.Dumper;
import com.github.dreamhead.moco.dumper.HttpRequestDumper;
import com.github.dreamhead.moco.dumper.HttpResponseDumper;
import com.github.dreamhead.moco.model.DefaultHttpRequest;
import com.github.dreamhead.moco.model.DefaultHttpResponse;
import com.github.dreamhead.moco.model.MessageContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ActionMonitor {
    private static Logger logger = LoggerFactory.getLogger(ActionMonitor.class);
    private final Dumper<Response> responseDumper = new HttpResponseDumper();
    private final Dumper<Request> requestDumper = new HttpRequestDumper();

    private String toPath(final URI uri) {
        final String path = uri.getPath();
        return path != null ? path : "/";
    }

    private Map<String, String[]> asQueries(final String query) {
        if (query == null || query.isEmpty()) {
            return Map.of();
        }

        // List rather than Set semantics: a repeated query parameter must not be collapsed.
        Map<String, List<String>> grouped = new LinkedHashMap<>();
        for (String param : query.split("&")) {
            String[] keyValue = param.split("=", 2);
            if (keyValue.length == 2) {
                grouped.computeIfAbsent(keyValue[0], key -> new ArrayList<>())
                        .add(java.net.URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8));
            } else if (keyValue.length == 1) {
                grouped.computeIfAbsent(keyValue[0], key -> new ArrayList<>()).add("");
            }
        }

        Map<String, String[]> result = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : grouped.entrySet()) {
            result.put(entry.getKey(), entry.getValue().toArray(new String[0]));
        }

        return result;
    }

    private static Map<String, Iterable<String>> asHeaders(final java.net.http.HttpHeaders httpHeaders) {
        Map<String, Iterable<String>> headers = new HashMap<>();
        httpHeaders.map().forEach((key, values) -> headers.put(key, values));
        return headers;
    }

    public final void postAction(final HttpResponse<byte[]> response) {
        final DefaultHttpResponse.Builder builder = DefaultHttpResponse.builder()
                .withVersion(HttpProtocolVersion.VERSION_1_1)
                .withStatus(response.statusCode())
                .withHeaders(asHeaders(response.headers()));

        byte[] content = response.body();
        if (content != null && content.length > 0) {
            builder.withContent(MessageContent.content().withContent(content).build());
        }

        logger.info("Action Response: {}\n", responseDumper.dump(builder.build()));
    }

    public final void preAction(final HttpRequest request) {
        final URI uri = request.uri();
        final DefaultHttpRequest.Builder builder = DefaultHttpRequest.builder()
                .withVersion(HttpProtocolVersion.VERSION_1_1)
                .withUri(toPath(uri))
                .withQueries(asQueries(uri.getQuery()))
                .withMethod(HttpMethod.valueOf(request.method().toUpperCase()))
                .withHeaders(asHeaders(request.headers()));

        // HttpRequest.BodyPublisher body is not directly accessible
        // Skip content logging for now as it's complex to extract from BodyPublisher
        logger.info("Action Request:{}\n", requestDumper.dump(builder.build()));
    }
}
