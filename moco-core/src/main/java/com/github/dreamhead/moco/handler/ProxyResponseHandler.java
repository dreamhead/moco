package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.ResponseHandler;
import org.apache.http.Header;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.util.EntityUtils;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class ProxyResponseHandler implements ResponseHandler {
    private URL url;

    public ProxyResponseHandler(URL url) {
        this.url = url;
    }

    @Override
    public void writeToResponse(HttpRequest request, HttpResponse response) {
        try {
            URL url = remoteUrl(request);

            Request targetRequest = createRequest(url, request);
            HttpVersion protocolVersion = request.getProtocolVersion();
            targetRequest.version(new org.apache.http.HttpVersion(protocolVersion.getMajorVersion(), protocolVersion.getMinorVersion()));
            for (Map.Entry<String, String> entry : request.getHeaders()) {
                targetRequest.addHeader(entry.getKey(), entry.getValue());
            }
            targetRequest.removeHeaders("Content-Length");

            long contentLength = HttpHeaders.getContentLength(request, -1);
            if (contentLength > 0) {
                targetRequest.bodyByteArray(request.getContent().array());
            }

            org.apache.http.HttpResponse targetResponse = targetRequest.config(ClientPNames.HANDLE_REDIRECTS, false).execute().returnResponse();
            response.setStatus(HttpResponseStatus.valueOf(targetResponse.getStatusLine().getStatusCode()));

            Header[] allHeaders = targetResponse.getAllHeaders();
            for (Header header : allHeaders) {
                response.setHeader(header.getName(), header.getValue());
            }

            ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
            buffer.writeBytes(EntityUtils.toByteArray(targetResponse.getEntity()));
            response.setContent(buffer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseHandler apply(final MocoConfig config) {
        return this;
    }

    private Request createRequest(URL url, HttpRequest request) {
        if (request.getMethod() == HttpMethod.GET) {
            return Request.Get(url.toString());
        }

        if (request.getMethod() == HttpMethod.POST) {
            return Request.Post(url.toString());
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
