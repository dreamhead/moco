package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.handler.failover.Failover;
import com.github.dreamhead.moco.internal.SessionContext;
import com.google.common.base.Optional;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static com.github.dreamhead.moco.util.ByteBufs.asBytes;
import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;
import static com.google.common.io.ByteStreams.toByteArray;

public abstract class AbstractProxyResponseHandler implements ResponseHandler {

    protected abstract Optional<String> remoteUrl(String uri) throws MalformedURLException;

    private static Logger logger = LoggerFactory.getLogger(AbstractProxyResponseHandler.class);

    protected final Failover failover;

    public AbstractProxyResponseHandler(Failover failover) {
        this.failover = failover;
    }

    protected HttpRequestBase prepareRemoteRequest(FullHttpRequest request, URL url) {
        HttpRequestBase remoteRequest = createRemoteRequest(request, url);
        RequestConfig config = RequestConfig.custom().setRedirectsEnabled(false).build();
        remoteRequest.setConfig(config);
        remoteRequest.setProtocolVersion(createVersion(request));

        long contentLength = HttpHeaders.getContentLength(request, -1);
        if (contentLength > 0 && remoteRequest instanceof HttpEntityEnclosingRequest) {
            HttpEntityEnclosingRequest entityRequest = (HttpEntityEnclosingRequest) remoteRequest;
            entityRequest.setEntity(createEntity(request.content(), contentLength));
        }

        return remoteRequest;
    }

    private HttpRequestBase createRemoteRequest(FullHttpRequest request, URL url) {
        HttpRequestBase remoteRequest = createBaseRequest(url, request.getMethod());
        for (Map.Entry<String, String> entry : request.headers()) {
            if (isRemoteHeader(entry)) {
                remoteRequest.addHeader(entry.getKey(), entry.getValue());
            }
        }

        return remoteRequest;
    }

    private HttpEntity createEntity(ByteBuf content, long contentLength) {
        return new ByteArrayEntity(asBytes(content), 0, (int) contentLength);
    }

    private org.apache.http.HttpVersion createVersion(FullHttpRequest request) {
        HttpVersion protocolVersion = request.getProtocolVersion();
        return new org.apache.http.HttpVersion(protocolVersion.majorVersion(), protocolVersion.minorVersion());
    }

    private boolean isRemoteHeader(Map.Entry<String, String> entry) {
        return !isHeader(entry, "Host") && !isHeader(entry, "Content-Length");
    }

    private boolean isHeader(Map.Entry<String, String> entry, String key) {
        return key.equalsIgnoreCase(entry.getKey());
    }

    private HttpRequestBase createBaseRequest(URL url, HttpMethod method) {
        if (method == HttpMethod.GET) {
            return new HttpGet(url.toString());
        }

        if (method == HttpMethod.POST) {
            return new HttpPost(url.toString());
        }

        if (method == HttpMethod.PUT) {
            return new HttpPut(url.toString());
        }

        if (method == HttpMethod.DELETE) {
            return new HttpDelete(url.toString());
        }

        if (method == HttpMethod.HEAD) {
            return new HttpHead(url.toString());
        }

        if (method == HttpMethod.OPTIONS) {
            return new HttpOptions(url.toString());
        }

        if (method == HttpMethod.TRACE) {
            return new HttpTrace(url.toString());
        }

        throw new RuntimeException("unknown HTTP method");
    }

    protected void setupResponse(HttpRequest request,
                                 FullHttpResponse response,
                                 org.apache.http.HttpResponse remoteResponse) throws IOException {
        int statusCode = remoteResponse.getStatusLine().getStatusCode();
        if (statusCode == HttpResponseStatus.BAD_REQUEST.code()) {
            failover.failover(request, response);
            return;
        }

        setupNormalResponse(response, remoteResponse);

        failover.onCompleteResponse(request, response);
    }

    private void setupNormalResponse(FullHttpResponse response, org.apache.http.HttpResponse remoteResponse) throws IOException {
        response.setProtocolVersion(HttpVersion.valueOf(remoteResponse.getProtocolVersion().toString()));
        response.setStatus(HttpResponseStatus.valueOf(remoteResponse.getStatusLine().getStatusCode()));

        Header[] allHeaders = remoteResponse.getAllHeaders();
        for (Header header : allHeaders) {
            response.headers().set(header.getName(), header.getValue());
        }

        HttpEntity entity = remoteResponse.getEntity();
        if (entity != null && entity.getContentLength() > 0) {
            ByteBuf buffer = Unpooled.copiedBuffer(toByteArray(entity.getContent()), 0, (int) entity.getContentLength());
            response.content().writeBytes(buffer);
        }
    }

    @Override
    public ResponseHandler apply(final MocoConfig config) {
        return this;
    }

    @Override
    public void writeToResponse(SessionContext context) {
        HttpRequest request = context.getRequest();
        FullHttpResponse response = context.getResponse();
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            FullHttpRequest httpRequest = ((com.github.dreamhead.moco.model.DefaultHttpRequest) context.getRequest()).toFullHttpRequest();
            Optional<URL> url = remoteUrl(httpRequest);
            if (!url.isPresent()) {
                return;
            }

            setupResponse(request, response, httpclient.execute(prepareRemoteRequest(httpRequest, url.get())));
        } catch (IOException e) {
            logger.error("Failed to load remote and try to failover", e);
            failover.failover(request, response);
        } finally {
            try {
                httpclient.close();
            } catch (IOException ignored) {
            }
        }
    }

    protected Optional<URL> remoteUrl(FullHttpRequest request) throws MalformedURLException {
        QueryStringDecoder decoder = new QueryStringDecoder(request.getUri());
        Optional<String> remoteUrl = this.remoteUrl(decoder.path());
        if (!remoteUrl.isPresent()) {
            return absent();
        }

        QueryStringEncoder encoder = new QueryStringEncoder(remoteUrl.get());

        for (Map.Entry<String, List<String>> entry : decoder.parameters().entrySet()) {
            encoder.addParam(entry.getKey(), entry.getValue().get(0));
        }

        return of(new URL(encoder.toString()));
    }
}
