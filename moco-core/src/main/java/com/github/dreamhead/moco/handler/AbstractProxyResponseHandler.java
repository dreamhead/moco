package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.HttpResponse;
import com.github.dreamhead.moco.MocoException;
import com.github.dreamhead.moco.MutableHttpResponse;
import com.github.dreamhead.moco.handler.failover.Failover;
import com.github.dreamhead.moco.model.DefaultHttpRequest;
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
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static com.github.dreamhead.moco.model.DefaultHttpResponse.newResponse;
import static com.github.dreamhead.moco.util.URLs.toUrl;
import static com.google.common.net.HttpHeaders.CONTENT_LENGTH;
import static com.google.common.net.HttpHeaders.DATE;
import static com.google.common.net.HttpHeaders.HOST;
import static com.google.common.net.HttpHeaders.SERVER;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.apache.http.util.EntityUtils.toByteArray;

public abstract class AbstractProxyResponseHandler extends AbstractHttpResponseHandler {

    private static final ImmutableSet<String> IGNORED_REQUEST_HEADERS = ImmutableSet.of(
            HOST.toUpperCase(), CONTENT_LENGTH.toUpperCase());
    private static final ImmutableSet<String> IGNORED_RESPONSE_HEADERS = ImmutableSet.of(
            DATE.toUpperCase(), SERVER.toUpperCase());

    private static CloseableHttpClient client;

    static {
        // Try to ignore SSL certification
        // https://memorynotfound.com/ignore-certificate-errors-apache-httpclient/
        try {
            PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
            SSLContext sslContext = SSLContextBuilder.create()
                    .loadTrustMaterial(new TrustSelfSignedStrategy())
                    .build();

            HostnameVerifier allowAllHosts = new NoopHostnameVerifier();
            SSLConnectionSocketFactory connectionFactory = new SSLConnectionSocketFactory(sslContext, allowAllHosts);

            client = HttpClients.custom()
                    .setConnectionManager(connManager)
                    .setConnectionManagerShared(true)
                    .setSSLSocketFactory(connectionFactory)
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

    private HttpRequestBase prepareRemoteRequest(final FullHttpRequest request, final URL url) {
        HttpRequestBase remoteRequest = createRemoteRequest(request, url);
        remoteRequest.setConfig(createRequestConfig());

        long contentLength = HttpUtil.getContentLength(request, -1);
        if (contentLength > 0 && remoteRequest instanceof HttpEntityEnclosingRequest) {
            HttpEntityEnclosingRequest entityRequest = (HttpEntityEnclosingRequest) remoteRequest;
            entityRequest.setEntity(createEntity(request.content(), contentLength));
        }

        return remoteRequest;
    }

    @SuppressWarnings("deprecation")
    private RequestConfig createRequestConfig() {
        return RequestConfig.custom()
                .setRedirectsEnabled(false)
                .setSocketTimeout(0)
                .setStaleConnectionCheckEnabled(true)
                .build();
    }

    private HttpRequestBase createRemoteRequest(final FullHttpRequest request, final URL url) {
        HttpRequestBase remoteRequest = createBaseRequest(url, request.method());

        remoteRequest.setProtocolVersion(createVersion(request));
        for (Map.Entry<String, String> entry : request.headers()) {
            if (isRequestHeader(entry)) {
                remoteRequest.addHeader(entry.getKey(), entry.getValue());
            }
        }

        return remoteRequest;
    }

    private HttpEntity createEntity(final ByteBuf content, final long contentLength) {
        return new InputStreamEntity(new ByteBufInputStream(content), contentLength);
    }

    private org.apache.http.HttpVersion createVersion(final FullHttpRequest request) {
        HttpVersion protocolVersion = request.protocolVersion();
        return new org.apache.http.HttpVersion(protocolVersion.majorVersion(), protocolVersion.minorVersion());
    }

    private boolean isRequestHeader(final Map.Entry<String, String> entry) {
        return !IGNORED_REQUEST_HEADERS.contains(entry.getKey().toUpperCase());
    }

    private boolean isResponseHeader(final Header header) {
        return !IGNORED_RESPONSE_HEADERS.contains(header.getName().toUpperCase());
    }

    private HttpRequestBase createBaseRequest(final URL url, final HttpMethod method) {
        String uri = url.toString();
        if (method == HttpMethod.GET) {
            return new HttpGet(uri);
        }

        if (method == HttpMethod.POST) {
            return new HttpPost(uri);
        }

        if (method == HttpMethod.PUT) {
            return new HttpPut(uri);
        }

        if (method == HttpMethod.DELETE) {
            return new HttpDelete(uri);
        }

        if (method == HttpMethod.HEAD) {
            return new HttpHead(uri);
        }

        if (method == HttpMethod.OPTIONS) {
            return new HttpOptions(uri);
        }

        if (method == HttpMethod.TRACE) {
            return new HttpTrace(uri);
        }

        if (method == HttpMethod.PATCH) {
            return new HttpPatch(uri);
        }

        throw new MocoException("unknown HTTP method");
    }

    private HttpResponse setupResponse(final HttpRequest request,
                                       final CloseableHttpResponse remoteResponse) throws IOException {
        if (failover.shouldFailover(remoteResponse)) {
            return failover.failover(request);
        }

        HttpResponse httpResponse = setupNormalResponse(remoteResponse);

        failover.onCompleteResponse(request, httpResponse);
        return httpResponse;
    }

    private HttpResponse setupNormalResponse(final CloseableHttpResponse remoteResponse) throws IOException {
        HttpVersion httpVersion = HttpVersion.valueOf(remoteResponse.getProtocolVersion().toString());
        HttpResponseStatus status = HttpResponseStatus.valueOf(remoteResponse.getStatusLine().getStatusCode());
        FullHttpResponse response = new DefaultFullHttpResponse(httpVersion, status);
        response.setStatus(status);

        Arrays.stream(remoteResponse.getAllHeaders())
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
            HttpResponse response = doProxy(httpRequest, actual);
            doWritHttpResponse(response, httpResponse);
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

    private HttpResponse doProxy(final HttpRequest request, final URL remoteUrl) {
        if (failover.isPlayback()) {
            try {
                return failover.failover(request);
            } catch (RuntimeException ignored) {
            }
        }

        return doForward(request, remoteUrl);
    }

    private HttpResponse doForward(final HttpRequest request, final URL remoteUrl) {
        try {
            HttpRequestBase remoteRequest = prepareRemoteRequest(request, remoteUrl);
            return setupResponse(request, client.execute(remoteRequest));
        } catch (ClientProtocolException e) {
            logger.error("Failed to create remote request", e);
            throw new MocoException(e);
        } catch (IOException e) {
            logger.error("Failed to load remote and try to failover", e);
            return failover.failover(request);
        } finally {
            try {
                client.close();
            } catch (IOException ignored) {
            }
        }
    }

    private HttpRequestBase prepareRemoteRequest(final HttpRequest request, final URL remoteUrl) {
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
}
