package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.HttpProtocolVersion;
import com.github.dreamhead.moco.MocoException;
import com.github.dreamhead.moco.MutableHttpResponse;
import com.github.dreamhead.moco.handler.failover.Failover;
import com.github.dreamhead.moco.model.DefaultHttpRequest;
import com.github.dreamhead.moco.model.DefaultHttpResponse;
import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.sse.SseEvent;
import com.github.dreamhead.moco.sse.SseEventParser;
import com.github.dreamhead.moco.util.ReaderLineIterator;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringEncoder;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import static com.github.dreamhead.moco.util.HttpHeaders.CACHE_CONTROL;
import static com.github.dreamhead.moco.util.HttpHeaders.CONNECTION;
import static com.github.dreamhead.moco.util.HttpHeaders.CONTENT_LENGTH;
import static com.github.dreamhead.moco.util.HttpHeaders.CONTENT_TYPE;
import static com.github.dreamhead.moco.util.HttpHeaders.DATE;
import static com.github.dreamhead.moco.util.HttpHeaders.HOST;
import static com.github.dreamhead.moco.util.HttpHeaders.SERVER;
import static com.github.dreamhead.moco.util.URLs.toUrl;
import static java.util.Optional.empty;
import static java.util.Optional.of;

/**
 * Proxy response handler using Java 17's native HttpClient.
 * Note: This implementation does not support HTTP/1.0, only HTTP/1.1 and HTTP/2.
 */
public abstract class AbstractProxyResponseHandler extends AbstractHttpResponseHandler {

    private static final ImmutableSet<String> IGNORED_REQUEST_HEADERS = ImmutableSet.of(
            HOST.toUpperCase(),
            CONTENT_LENGTH.toUpperCase(),
            "CONNECTION",
            "EXPECT",
            "UPGRADE");
    private static final ImmutableSet<String> IGNORED_RESPONSE_HEADERS = ImmutableSet.of(
            DATE.toUpperCase(), SERVER.toUpperCase());

    private static final Logger logger = LoggerFactory.getLogger(AbstractProxyResponseHandler.class);

    private final Failover failover;

    protected AbstractProxyResponseHandler(final Failover failover) {
        this.failover = failover;
    }

    private HttpClient createClient() {
        try {
            // Create SSL context that trusts all certificates
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{
                new TrustAllX509TrustManager()
            }, new SecureRandom());

            return HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .sslContext(sslContext)
                    .connectTimeout(Duration.ofSeconds(30))
                    .followRedirects(HttpClient.Redirect.NEVER)
                    .build();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new MocoException(e);
        }
    }

    protected abstract Optional<String> doRemoteUrl(com.github.dreamhead.moco.HttpRequest request);

    private HttpRequest.BodyPublisher bodyPublisher(final ByteBuf content) {
        if (content == null || !content.isReadable()) {
            return HttpRequest.BodyPublishers.noBody();
        }

        byte[] bytes = new byte[content.readableBytes()];
        content.readBytes(bytes);
        return HttpRequest.BodyPublishers.ofByteArray(bytes);
    }

    private HttpRequest createRemoteRequest(final FullHttpRequest request, final URL url) {
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(url.toURI())
                    .method(request.method().name(), bodyPublisher(request.content()));

            for (Map.Entry<String, String> entry : request.headers()) {
                if (isRequestHeader(entry)) {
                    builder.header(entry.getKey(), entry.getValue());
                }
            }

            return builder.build();
        } catch (URISyntaxException e) {
            throw new MocoException(e);
        }
    }

    private boolean isRequestHeader(final Map.Entry<String, String> entry) {
        return !IGNORED_REQUEST_HEADERS.contains(entry.getKey().toUpperCase());
    }

    private boolean isResponseHeader(final String name) {
        return !IGNORED_RESPONSE_HEADERS.contains(name.toUpperCase());
    }

    private void writeSseEvents(final Iterable<SseEvent> events, final MutableHttpResponse httpResponse) {
        httpResponse.addHeader(CONTENT_TYPE, "text/event-stream");
        httpResponse.addHeader(CACHE_CONTROL, "no-cache");
        httpResponse.addHeader(CONNECTION, "keep-alive");
        httpResponse.addHeader("X-Accel-Buffering", "no");
        httpResponse.setSseEvents(events);
    }

    private com.github.dreamhead.moco.HttpResponse toHttpResponse(final HttpResponse<InputStream> response) throws IOException {
        HttpProtocolVersion version = parseVersion(response.version());
        int status = response.statusCode();

        Map<String, String[]> headers = extractResponseHeaders(response);
        byte[] content = readResponseContent(response.body());
        MessageContent messageContent = content.length > 0 ? MessageContent.content().withContent(content).build() : null;

        return DefaultHttpResponse.builder()
                .withVersion(version)
                .withStatus(status)
                .withHeaders(headers)
                .withContent(messageContent)
                .build();
    }

    private Map<String, String[]> extractResponseHeaders(final HttpResponse<InputStream> response) {
        Map<String, String[]> headers = new HashMap<>();
        response.headers().map().entrySet().stream()
                .filter(header -> isResponseHeader(header.getKey()))
                .forEach(header -> {
                    for (String value : header.getValue()) {
                        mergeHeader(headers, header.getKey(), value);
                    }
                });
        return headers;
    }

    private byte[] readResponseContent(final InputStream body) throws IOException {
        try (body) {
            return body.readAllBytes();
        }
    }

    private void mergeHeader(final Map<String, String[]> headers, final String name, final String value) {
        String[] existing = headers.get(name);
        if (existing == null) {
            headers.put(name, new String[]{value});
        } else {
            String[] merged = Arrays.copyOf(existing, existing.length + 1);
            merged[existing.length] = value;
            headers.put(name, merged);
        }
    }

    private static HttpProtocolVersion parseVersion(final HttpClient.Version version) {
        if (version == HttpClient.Version.HTTP_1_1) {
            return HttpProtocolVersion.VERSION_1_1;
        } else if (version == HttpClient.Version.HTTP_2) {
            return HttpProtocolVersion.VERSION_2_0;
        }
        return HttpProtocolVersion.VERSION_1_1;  // Default fallback
    }

    @Override
    protected final void doWriteToResponse(final com.github.dreamhead.moco.HttpRequest httpRequest, final MutableHttpResponse httpResponse) {
        Optional<URL> url = remoteUrl(httpRequest);
        url.ifPresent(actual -> doProxy(httpRequest, actual, httpResponse));
    }

    private void writeNormalResponse(final com.github.dreamhead.moco.HttpResponse response, final MutableHttpResponse httpResponse) {
        httpResponse.setVersion(response.getVersion());
        httpResponse.setStatus(response.getStatus());
        for (Map.Entry<String, String[]> entry : response.getHeaders().entrySet()) {
            String key = entry.getKey();
            for (String value : entry.getValue()) {
                httpResponse.addHeader(key, value);
            }
        }

        httpResponse.setContent(response.getContent());
    }

    private void doProxy(final com.github.dreamhead.moco.HttpRequest request, final URL remoteUrl, final MutableHttpResponse httpResponse) {
        if (failover.isPlayback()) {
            try {
                writeResponseFromFailover(failover.failover(request), httpResponse);
                return;
            } catch (RuntimeException ignored) {
            }
        }

        doForward(request, remoteUrl, httpResponse);
    }

    private void doForward(final com.github.dreamhead.moco.HttpRequest request, final URL remoteUrl, final MutableHttpResponse httpResponse) {
        HttpClient client = createClient();
        try {
            FullHttpRequest httpRequest = ((DefaultHttpRequest) request).toFullHttpRequest();
            HttpRequest remoteRequest = createRemoteRequest(httpRequest, remoteUrl);
            HttpResponse<InputStream> response = client.send(remoteRequest,
                    HttpResponse.BodyHandlers.ofInputStream());

            writeResponse(request, response, httpResponse);
        } catch (IOException e) {
            logger.error("Failed to load remote and try to failover", e);
            writeResponseFromFailover(failover.failover(request), httpResponse);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Request interrupted", e);
            throw new MocoException(e);
        }
    }

    private void writeResponse(final com.github.dreamhead.moco.HttpRequest request,
                               final HttpResponse<InputStream> response,
                               final MutableHttpResponse httpResponse) throws IOException {
        if (failover.shouldFailover(response.statusCode())) {
            response.body().close();
            writeResponseFromFailover(failover.failover(request), httpResponse);
            return;
        }

        if (isSseResponse(response)) {
            writeSseResponse(request, response, httpResponse);
            return;
        }

        com.github.dreamhead.moco.HttpResponse normalResponse = toHttpResponse(response);
        failover.onCompleteResponse(request, normalResponse);
        writeNormalResponse(normalResponse, httpResponse);
    }

    private boolean isSseResponse(final HttpResponse<?> response) {
        return response.headers().firstValue("Content-Type")
                .map(ct -> ct.contains("text/event-stream"))
                .orElse(false);
    }

    private void writeSseResponse(final com.github.dreamhead.moco.HttpRequest request,
                                    final HttpResponse<InputStream> streamingResponse,
                                    final MutableHttpResponse httpResponse) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(streamingResponse.body(), StandardCharsets.UTF_8));
        ReaderLineIterator lineIterator = new ReaderLineIterator(reader);
        Iterable<String> lines = () -> lineIterator;
        Iterable<SseEvent> events = new SseEventParser().parse(lines);

        SseEventConsumer consumer = getSseEventConsumer(request, streamingResponse);

        writeSseEvents(new SseEventStreamIterable(events, reader, consumer), httpResponse);
    }

    private SseEventConsumer getSseEventConsumer(final com.github.dreamhead.moco.HttpRequest request,
                                                      final HttpResponse<InputStream> response) {
        if (failover.hasFailover()) {
            return collected -> {
                com.github.dreamhead.moco.HttpResponse mocoResponse = DefaultHttpResponse.builder()
                        .withVersion(parseVersion(response.version()))
                        .withStatus(response.statusCode())
                        .withSseEvents(collected)
                        .build();
                failover.onCompleteResponse(request, mocoResponse);
            };
        }
        return null;
    }

    private void writeResponseFromFailover(final com.github.dreamhead.moco.HttpResponse response, final MutableHttpResponse httpResponse) {
        if (response.getSseEvents() != null) {
            writeSseEvents(response.getSseEvents(), httpResponse);
            return;
        }

        writeNormalResponse(response, httpResponse);
    }

    private Optional<URL> remoteUrl(final com.github.dreamhead.moco.HttpRequest request) {
        Optional<String> remoteUrl = this.doRemoteUrl(request);
        return remoteUrl.flatMap(actual -> doGetRemoteUrl(request, actual));
    }

    private Optional<URL> doGetRemoteUrl(final com.github.dreamhead.moco.HttpRequest request, final String actual) {
        try {
            return of(toUrl(getQueryStringEncoder(request, actual).toString()));
        } catch (IllegalArgumentException e) {
            return empty();
        }
    }

    private QueryStringEncoder getQueryStringEncoder(final com.github.dreamhead.moco.HttpRequest request, final String actual) {
        QueryStringEncoder encoder = new QueryStringEncoder(actual);
        for (Map.Entry<String, String[]> entry : request.getQueries().entrySet()) {
            for (String value : entry.getValue()) {
                encoder.addParam(entry.getKey(), value);
            }
        }

        return encoder;
    }

    protected final Failover failover() {
        return failover;
    }

    // Inner classes
    private static class TrustAllX509TrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    @FunctionalInterface
    interface SseEventConsumer {
        void accept(ImmutableList<SseEvent> events);
    }

    private static final class SseEventStreamIterable implements Iterable<SseEvent> {
        private final Iterable<SseEvent> events;
        private final Closeable closeable;
        private final SseEventConsumer onEvent;
        private boolean iterated;

        SseEventStreamIterable(final Iterable<SseEvent> events,
                               final Closeable closeable,
                               final SseEventConsumer onEvent) {
            this.events = events;
            this.closeable = closeable;
            this.onEvent = onEvent;
        }

        @Override
        public @NonNull Iterator<SseEvent> iterator() {
            if (iterated) {
                throw new IllegalStateException("SSE events can only be iterated once");
            }
            iterated = true;
            return new Iterator<>() {
                private final java.util.Iterator<SseEvent> delegate = events.iterator();
                private final ImmutableList.Builder<SseEvent> collected = ImmutableList.builder();
                private boolean closed;

                @Override
                public boolean hasNext() {
                    try {
                        boolean hasMore = delegate.hasNext();
                        if (!hasMore) {
                            close();
                        }
                        return hasMore;
                    } catch (Exception e) {
                        close();
                        return false;
                    }
                }

                @Override
                public SseEvent next() {
                    SseEvent event = delegate.next();
                    if (onEvent != null) {
                        collected.add(event);
                        onEvent.accept(collected.build());
                    }
                    return event;
                }

                private void close() {
                    if (closed) {
                        return;
                    }
                    closed = true;
                    try {
                        closeable.close();
                    } catch (IOException ignored) {
                    }
                }
            };
        }
    }
}
