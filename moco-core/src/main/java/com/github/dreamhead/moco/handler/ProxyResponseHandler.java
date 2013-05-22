package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.ResponseHandler;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static com.google.common.io.ByteStreams.toByteArray;

public class ProxyResponseHandler implements ResponseHandler {
    private URL url;

    public ProxyResponseHandler(URL url) {
        this.url = url;
    }

    @Override
    public void writeToResponse(HttpRequest request, HttpResponse response) {
        try {
            URL url = remoteUrl(request);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(request.getMethod().toString());
            urlConnection.setDoInput(true);

            prepareHeader(request, urlConnection);
            prepareContent(request, urlConnection);

            writeResponse(response, urlConnection);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void prepareContent(HttpRequest request, HttpURLConnection urlConnection) throws IOException {
        long contentLength = HttpHeaders.getContentLength(request, -1);
        if (contentLength > 0) {
            urlConnection.setDoOutput(true);
            OutputStream os = urlConnection.getOutputStream();
            os.write(request.getContent().array());
        }
    }

    private void prepareHeader(HttpRequest request, HttpURLConnection urlConnection) {
        for (Map.Entry<String, String> entry : request.getHeaders()) {
            urlConnection.setRequestProperty(entry.getKey(), entry.getValue());
        }
    }

    private void writeResponse(HttpResponse response, HttpURLConnection urlConnection) throws IOException {
        int responseCode = urlConnection.getResponseCode();
        response.setStatus(HttpResponseStatus.valueOf(responseCode));
        if (responseCode == HttpResponseStatus.OK.getCode()) {
            InputStream inputStream = urlConnection.getInputStream();
            ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
            buffer.writeBytes(toByteArray(inputStream));
            response.setContent(buffer);
            response.setHeader("Content-Length", response.getContent().writerIndex());
        }
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
