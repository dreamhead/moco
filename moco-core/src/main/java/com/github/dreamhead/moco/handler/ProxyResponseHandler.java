package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.ResponseHandler;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod(request.getMethod().toString());
            urlConnection.setDoInput(true);

            for (Map.Entry<String, String> entry : request.getHeaders()) {
                urlConnection.setRequestProperty(entry.getKey(), entry.getValue());
            }

            long contentLength = HttpHeaders.getContentLength(request, -1);
            if (contentLength > 0) {
                urlConnection.setDoOutput(true);
                OutputStream os = urlConnection.getOutputStream();
                os.write(request.getContent().array());
            }

            int responseCode = urlConnection.getResponseCode();
            response.setStatus(HttpResponseStatus.valueOf(responseCode));
            if (responseCode == HttpResponseStatus.OK.getCode()) {
                InputStream inputStream = urlConnection.getInputStream();
                ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
                buffer.writeBytes(toByteArray(inputStream));
                response.setContent(buffer);
                response.setHeader("Content-Length", response.getContent().writerIndex());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
