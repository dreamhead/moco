package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.RequestExtractor;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;

import java.nio.charset.Charset;

public class ContentRequestExtractor implements RequestExtractor {
    @Override
    public String extract(HttpRequest request) {
        return requestContent(request);
    }

    private String requestContent(HttpRequest request) {
        return retrieveContent(request.getContent(), HttpHeaders.getContentLength(request));
    }

    private String retrieveContent(ChannelBuffer contentBuffer, long contentLength) {
        return contentBuffer.toString(contentStartIndex(contentLength, contentBuffer), (int) contentLength, Charset.defaultCharset());
    }

    private int contentStartIndex(long contentLength, ChannelBuffer contentBuffer) {
        return (int) (contentBuffer.capacity() - contentLength);
    }
}
