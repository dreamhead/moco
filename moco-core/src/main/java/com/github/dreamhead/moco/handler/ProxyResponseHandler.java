package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.handler.failover.Failover;
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

import static com.google.common.io.ByteStreams.toByteArray;

public class ProxyResponseHandler implements ResponseHandler {
    private Logger logger = LoggerFactory.getLogger(ProxyResponseHandler.class);
    private final URL url;
    private final Failover failover;

    public ProxyResponseHandler(URL url, Failover failover) {
        this.url = url;
        this.failover = failover;
    }

    @Override
    public void writeToResponse(FullHttpRequest request, FullHttpResponse response) {
        try {
            URL url = remoteUrl(request);

            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpRequestBase remoteRequest = createRemoteRequest(request, url);
            RequestConfig config = RequestConfig.custom().setRedirectsEnabled(false).build();
            remoteRequest.setConfig(config);
            remoteRequest.setProtocolVersion(createVersion(request));


            long contentLength = HttpHeaders.getContentLength(request, -1);
            if (contentLength > 0 && remoteRequest instanceof HttpEntityEnclosingRequest) {
                HttpEntityEnclosingRequest entityRequest = (HttpEntityEnclosingRequest) remoteRequest;
                entityRequest.setEntity(new ByteArrayEntity(request.content().array()));
            }

            setupResponse(request, response, httpclient.execute(remoteRequest));
        } catch (IOException e) {
            logger.error("Failed to load remote and try to failover", e);
            failover.failover(request, response);
        }
    }

    private org.apache.http.HttpVersion createVersion(HttpRequest request) {
        HttpVersion protocolVersion = request.getProtocolVersion();
        return new org.apache.http.HttpVersion(protocolVersion.majorVersion(), protocolVersion.minorVersion());
    }

    private void setupResponse(FullHttpRequest request,
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

    private HttpRequestBase createRemoteRequest(HttpRequest request, URL url) {
        HttpRequestBase remoteRequest = createBaseRequest(url, request);
        for (Map.Entry<String, String> entry : request.headers()) {
            if (isRemoteHeader(entry)) {
                remoteRequest.addHeader(entry.getKey(), entry.getValue());
            }
        }

        return remoteRequest;
    }

    private boolean isRemoteHeader(Map.Entry<String, String> entry) {
        return !isHeader(entry, "Host") && !isHeader(entry, "Content-Length");

    }

    private boolean isHeader(Map.Entry<String, String> entry, String key) {
        return key.equalsIgnoreCase(entry.getKey());
    }

    @Override
    public ResponseHandler apply(final MocoConfig config) {
        return this;
    }

    private HttpRequestBase createBaseRequest(URL url, HttpRequest request) {
        return createBaseRequest(url, request.getMethod());
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

    private URL remoteUrl(HttpRequest request) throws MalformedURLException {
        QueryStringDecoder decoder = new QueryStringDecoder(request.getUri());
        QueryStringEncoder encoder = new QueryStringEncoder(this.url.getPath());

        for (Map.Entry<String, List<String>> entry : decoder.parameters().entrySet()) {
            encoder.addParam(entry.getKey(), entry.getValue().get(0));
        }

        return new URL(this.url.getProtocol(), this.url.getHost(), this.url.getPort(), encoder.toString());
    }
}
