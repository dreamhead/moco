package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.handler.failover.Failover;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class ProxyResponseHandler implements ResponseHandler {
    private final URL url;
    private final Failover failover;

    public ProxyResponseHandler(URL url, Failover failover) {
        this.url = url;
        this.failover = failover;
    }

    @Override
    public void writeToResponse(HttpRequest request, HttpResponse response) {
        try {
            URL url = remoteUrl(request);

            HttpClient httpclient = new DefaultHttpClient(createParams(request));

            HttpRequestBase remoteRequest = createRemoteRequest(request, url);

            long contentLength = HttpHeaders.getContentLength(request, -1);
            if (contentLength > 0 && remoteRequest instanceof HttpEntityEnclosingRequest) {
                HttpEntityEnclosingRequest entityRequest = (HttpEntityEnclosingRequest) remoteRequest;
                entityRequest.setEntity(new ByteArrayEntity(request.getContent().array()));
            }

            setupResponse(request, response, httpclient.execute(remoteRequest));
        } catch (IOException e) {
            failover.failover(request, response);
        }
    }

    private BasicHttpParams createParams(HttpRequest request) {
        BasicHttpParams params = new BasicHttpParams();
        params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, createVersion(request));
        params.setParameter(ClientPNames.HANDLE_REDIRECTS, false);
        return params;
    }

    private org.apache.http.HttpVersion createVersion(HttpRequest request) {
        HttpVersion protocolVersion = request.getProtocolVersion();
        return new org.apache.http.HttpVersion(protocolVersion.getMajorVersion(), protocolVersion.getMinorVersion());
    }

    private void setupResponse(HttpRequest request,
                               HttpResponse response,
                               org.apache.http.HttpResponse remoteResponse) throws IOException {
        int statusCode = remoteResponse.getStatusLine().getStatusCode();
        if (statusCode == HttpResponseStatus.BAD_REQUEST.getCode()) {
            failover.failover(request, response);
            return;
        }

        setupNormalRequest(response, remoteResponse);

        failover.onCompleteResponse(request, response);
    }

    private void setupNormalRequest(HttpResponse response, org.apache.http.HttpResponse remoteResponse) throws IOException {
        response.setProtocolVersion(HttpVersion.valueOf(remoteResponse.getProtocolVersion().toString()));
        response.setStatus(HttpResponseStatus.valueOf(remoteResponse.getStatusLine().getStatusCode()));

        Header[] allHeaders = remoteResponse.getAllHeaders();
        for (Header header : allHeaders) {
            response.setHeader(header.getName(), header.getValue());
        }

        HttpEntity entity = remoteResponse.getEntity();
        if (entity != null) {
            ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
            buffer.writeBytes(entity.getContent(), (int)entity.getContentLength());
            response.setContent(buffer);
        }

    }

    private HttpRequestBase createRemoteRequest(HttpRequest request, URL url) {
        HttpRequestBase remoteRequest = createBaseRequest(url, request);
        for (Map.Entry<String, String> entry : request.getHeaders()) {
            remoteRequest.addHeader(entry.getKey(), entry.getValue());
        }

        remoteRequest.removeHeaders("Content-Length");
        return remoteRequest;
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

        for (Map.Entry<String, List<String>> entry : decoder.getParameters().entrySet()) {
            encoder.addParam(entry.getKey(), entry.getValue().get(0));
        }

        return new URL(this.url.getProtocol(), this.url.getHost(), this.url.getPort(), encoder.toString());
    }
}
