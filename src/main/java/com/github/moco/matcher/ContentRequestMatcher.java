package com.github.moco.matcher;

import com.github.moco.RequestMatcher;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;

import java.nio.charset.Charset;

public class ContentRequestMatcher implements RequestMatcher {
    private final String content;

    public ContentRequestMatcher(final String content) {
        this.content = content;
    }

    @Override
    public boolean match(HttpRequest request) {
        String content = retrieveContent(request.getContent(), HttpHeaders.getContentLength(request));

        return content.equals(this.content);
    }

    private String retrieveContent(ChannelBuffer contentBuffer, long contentLength) {
        return contentBuffer.toString(contentStartIndex(contentLength, contentBuffer), (int) contentLength, Charset.defaultCharset());
    }

    private int contentStartIndex(long contentLength, ChannelBuffer contentBuffer) {
        return (int) (contentBuffer.capacity() - contentLength);
    }
}
