package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.HttpResponse;
import com.github.dreamhead.moco.MocoException;
import com.github.dreamhead.moco.MutableHttpResponse;
import com.github.dreamhead.moco.handler.failover.Failover;
import com.github.dreamhead.moco.model.DefaultHttpRequest;
import com.github.dreamhead.moco.model.DefaultMutableHttpResponse;
import com.github.dreamhead.moco.sse.SseEvent;
import com.github.dreamhead.moco.sse.SseEventParser;
import com.github.dreamhead.moco.util.ReaderLineIterator;
import com.google.common.collect.ImmutableSet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringEncoder;
import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.URIScheme;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.http.io.entity.InputStreamEntity;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.github.dreamhead.moco.model.DefaultHttpResponse.newResponse;
import static com.github.dreamhead.moco.util.URLs.toUrl;
import static com.google.common.net.HttpHeaders.CACHE_CONTROL;
import static com.google.common.net.HttpHeaders.CONNECTION;
import static com.google.common.net.HttpHeaders.CONTENT_LENGTH;
import static com.google.common.net.HttpHeaders.CONTENT_TYPE;
import static com.google.common.net.HttpHeaders.DATE;
import static com.google.common.net.HttpHeaders.HOST;
import static com.google.common.net.HttpHeaders.SERVER;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.apache.hc.core5.http.io.entity.EntityUtils.toByteArray;

public abstract class AbstractProxyResponseHandler extends AbstractHttpResponseHandler {

    private static final ImmutableSet<String> IGNORED_REQUEST_HEADERS = ImmutableSet.of(
            HOST.toUpperCase(), CONTENT_LENGTH.toUpperCase());
    private static final ImmutableSet<String> IGNORED_RESPONSE_HEADERS = ImmutableSet.of(
            DATE.toUpperCase(), SERVER.toUpperCase());

    private CloseableHttpClient createClient() {
        // Try to ignore SSL certification
        // https://memorynotfound.com/ignore-certificate-errors-apache-httpclient/
        try {

            SSLContext sslContext = SSLContextBuilder.create()
                    .loadTrustMaterial(new TrustStrategy() {
                        @Override
                        public boolean isTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                            return true; // Trust all certificates
                        }
                    })
                    .build();

            HostnameVerifier allowAllHosts = new NoopHostnameVerifier();
            SSLConnectionSocketFactory connectionFactory = new SSLConnectionSocketFactory(sslContext, allowAllHosts);

            RegistryBuilder.<ConnectionSocketFactory>create()
                    .register(URIScheme.HTTP.id, PlainConnectionSocketFactory.getSocketFactory())
                    .register(URIScheme.HTTPS.id, connectionFactory)
                    .build();

            PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();

            return HttpClients.custom()
                    .setConnectionManager(connManager)
                    .setConnectionManagerShared(true)
                    .build();
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            throw new MocoException(e);
        }
    }

    protected abstract Optional<String> doRemoteUrl(HttpRequest request);

    private static Logger logger = LoggerFactory.getLogger(AbstractProxyResponseHandler.class);

    private final Failover failover;

    protected AbstractProxyResponseHandler(final Failover failover) {
        this.failover = failover;
    }

    private HttpUriRequestBase prepareRemoteRequest(final FullHttpRequest request, final URL url) {
        HttpUriRequestBase remoteRequest = createRemoteRequest(request, url);
        remoteRequest.setConfig(createRequestConfig());

        long contentLength = HttpUtil.getContentLength(request, -1);
        if (contentLength > 0) {
            remoteRequest.setEntity(createEntity(request.content(), contentLength));
        }

        return remoteRequest;
    }

    private RequestConfig createRequestConfig() {
        return RequestConfig.custom()
                .setRedirectsEnabled(false)
                .setResponseTimeout(0, TimeUnit.SECONDS)
//                .setSocketTimeout(0)
//                .setStaleConnectionCheckEnabled(true)
                .build();
    }

    private HttpUriRequestBase createRemoteRequest(final FullHttpRequest request, final URL url) {
        HttpUriRequestBase remoteRequest = createBaseRequest(url, request.method());

        remoteRequest.setVersion(createVersion(request));
        for (Map.Entry<String, String> entry : request.headers()) {
            if (isRequestHeader(entry)) {
                remoteRequest.addHeader(entry.getKey(), entry.getValue());
            }
        }

        return remoteRequest;
    }

    private HttpEntity createEntity(final ByteBuf content, final long contentLength) {
        return new InputStreamEntity(new ByteBufInputStream(content), contentLength, null);
    }

    private org.apache.hc.core5.http.HttpVersion createVersion(final FullHttpRequest request) {
        HttpVersion protocolVersion = request.protocolVersion();
        return new org.apache.hc.core5.http.HttpVersion(protocolVersion.majorVersion(), protocolVersion.minorVersion());
    }

    private boolean isRequestHeader(final Map.Entry<String, String> entry) {
        return !IGNORED_REQUEST_HEADERS.contains(entry.getKey().toUpperCase());
    }

    private boolean isResponseHeader(final Header header) {
        return !IGNORED_RESPONSE_HEADERS.contains(header.getName().toUpperCase());
    }

    private HttpUriRequestBase createBaseRequest(final URL url, final HttpMethod method) {
        try {
            return new HttpUriRequestBase(method.name(), url.toURI());
        } catch (URISyntaxException e) {
            throw new MocoException(e);
        }
    }

    private ProxyResult setupResponse(final HttpRequest request,
                                       final ClassicHttpResponse remoteResponse,
                                       final CloseableHttpClient client) throws IOException {
        if (failover.shouldFailover(remoteResponse)) {
            closeQuietly(remoteResponse);
            closeQuietly(client);
            return ProxyResult.normal(failover.failover(request));
        }

        if (isSseResponse(remoteResponse)) {
            return setupSseResult(remoteResponse, client);
        }

        HttpResponse httpResponse = setupNormalResponse(remoteResponse);
        failover.onCompleteResponse(request, httpResponse);
        closeQuietly(remoteResponse);
        closeQuietly(client);
        return ProxyResult.normal(httpResponse);
    }

    private ProxyResult setupSseResult(ClassicHttpResponse remoteResponse, CloseableHttpClient client) throws IOException {
        ReaderLineIterator lineIterator = new ReaderLineIterator(
                new InputStreamReader(remoteResponse.getEntity().getContent(), StandardCharsets.UTF_8));
        Iterable<String> lines = () -> lineIterator;
        Iterable<SseEvent> events = new SseEventParser().parse(lines);
        return ProxyResult.sse(new CloseOnExhaustIterable(events, () -> {
            closeQuietly(remoteResponse);
            closeQuietly(client);
        }));
    }

    private HttpResponse setupNormalResponse(final ClassicHttpResponse remoteResponse) throws IOException {
        HttpVersion httpVersion = HttpVersion.valueOf(remoteResponse.getVersion().toString());
        HttpResponseStatus status = HttpResponseStatus.valueOf(remoteResponse.getCode());
        FullHttpResponse response = new DefaultFullHttpResponse(httpVersion, status);
        response.setStatus(status);

        Arrays.stream(remoteResponse.getHeaders())
                .filter(this::isResponseHeader)
                .forEach(header -> response.headers().set(header.getName(), header.getValue()));

        HttpEntity entity = remoteResponse.getEntity();
        if (entity != null) {
            byte[] content = toByteArray(entity);
            if (content.length > 0) {
                ByteBuf buffer = Unpooled.copiedBuffer(content);
                response.content().writeBytes(buffer);
            }
        }

        return newResponse(response);
    }

    @Override
    protected final void doWriteToResponse(final HttpRequest httpRequest, final MutableHttpResponse httpResponse) {
        Optional<URL> url = remoteUrl(httpRequest);
        url.ifPresent(actual -> {
            ProxyResult result = doProxy(httpRequest, actual);
            if (result.isSse()) {
                writeSseResponse(result.getSseEvents(), httpResponse);
            } else {
                doWritHttpResponse(result.getResponse(), httpResponse);
            }
        });
    }

    private void doWritHttpResponse(final HttpResponse response, final MutableHttpResponse httpResponse) {
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

    private ProxyResult doProxy(final HttpRequest request, final URL remoteUrl) {
        if (failover.isPlayback()) {
            try {
                return ProxyResult.normal(failover.failover(request));
            } catch (RuntimeException ignored) {
            }
        }

        return doForward(request, remoteUrl);
    }

    private ProxyResult doForward(final HttpRequest request, final URL remoteUrl) {
        CloseableHttpClient client = createClient();
        CloseableHttpResponse remoteResponse = null;
        try {
            HttpUriRequestBase remoteRequest = prepareRemoteRequest(request, remoteUrl);
            remoteResponse = client.execute(remoteRequest);
            return setupResponse(request, remoteResponse, client);
        } catch (ClientProtocolException e) {
            closeQuietly(remoteResponse);
            closeQuietly(client);
            logger.error("Failed to create remote request", e);
            throw new MocoException(e);
        } catch (IOException e) {
            closeQuietly(remoteResponse);
            closeQuietly(client);
            logger.error("Failed to load remote and try to failover", e);
            return ProxyResult.normal(failover.failover(request));
        }
    }

    private void closeQuietly(final Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ignored) {
            }
        }
    }

    private HttpUriRequestBase prepareRemoteRequest(final HttpRequest request, final URL remoteUrl) {
        FullHttpRequest httpRequest = ((DefaultHttpRequest) request).toFullHttpRequest();
        return prepareRemoteRequest(httpRequest, remoteUrl);
    }

    private Optional<URL> remoteUrl(final HttpRequest request) {
        Optional<String> remoteUrl = this.doRemoteUrl(request);
        return remoteUrl.flatMap(actual -> doGetRemoteUrl(request, actual));
    }

    private Optional<URL> doGetRemoteUrl(final HttpRequest request, final String actual) {
        try {
            return of(toUrl(getQueryStringEncoder(request, actual).toString()));
        } catch (IllegalArgumentException e) {
            return empty();
        }
    }

    private QueryStringEncoder getQueryStringEncoder(final HttpRequest request, final String actual) {
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

    private void writeSseResponse(final Iterable<SseEvent> events, final MutableHttpResponse httpResponse) {
        httpResponse.addHeader(CONTENT_TYPE, "text/event-stream");
        httpResponse.addHeader(CACHE_CONTROL, "no-cache");
        httpResponse.addHeader(CONNECTION, "keep-alive");
        httpResponse.addHeader("X-Accel-Buffering", "no");

        if (httpResponse instanceof DefaultMutableHttpResponse) {
            ((DefaultMutableHttpResponse) httpResponse).setSseEvents(events);
        }
    }

    private boolean isSseResponse(final ClassicHttpResponse remoteResponse) {
        Header contentType = remoteResponse.getFirstHeader("Content-Type");
        return contentType != null && contentType.getValue().contains("text/event-stream");
    }

    private static final class CloseOnExhaustIterable implements Iterable<SseEvent> {
        private final Iterable<SseEvent> events;
        private final Closeable closeable;
        private boolean iterated;

        CloseOnExhaustIterable(final Iterable<SseEvent> events, final Closeable closeable) {
            this.events = events;
            this.closeable = closeable;
        }

        @Override
        public Iterator<SseEvent> iterator() {
            if (iterated) {
                throw new IllegalStateException("SSE events can only be iterated once");
            }
            iterated = true;
            return new Iterator<SseEvent>() {
                private final Iterator<SseEvent> delegate = events.iterator();

                @Override
                public boolean hasNext() {
                    boolean hasMore = delegate.hasNext();
                    if (!hasMore) {
                        closeQuietly();
                    }
                    return hasMore;
                }

                @Override
                public SseEvent next() {
                    return delegate.next();
                }
            };
        }

        private void closeQuietly() {
            try {
                closeable.close();
            } catch (IOException ignored) {
            }
        }
    }

    private static final class ProxyResult {
        private final HttpResponse response;
        private final Iterable<SseEvent> sseEvents;

        private ProxyResult(final HttpResponse response, final Iterable<SseEvent> sseEvents) {
            this.response = response;
            this.sseEvents = sseEvents;
        }

        static ProxyResult normal(final HttpResponse response) {
            return new ProxyResult(response, null);
        }

        static ProxyResult sse(final Iterable<SseEvent> events) {
            return new ProxyResult(null, events);
        }

        HttpResponse getResponse() {
            return response;
        }

        Iterable<SseEvent> getSseEvents() {
            return sseEvents;
        }

        boolean isSse() {
            return sseEvents != null;
        }
    }
}
