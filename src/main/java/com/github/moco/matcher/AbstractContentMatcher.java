package com.github.moco.matcher;

import com.github.moco.RequestMatcher;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;

import java.nio.charset.Charset;

public abstract class AbstractContentMatcher implements RequestMatcher {
    protected abstract boolean doMatch(String requestContent);

    @Override
    public boolean match(HttpRequest request) {
        return doMatch(requestContent(request));
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
