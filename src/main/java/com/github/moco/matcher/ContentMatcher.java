package com.github.moco.matcher;

import com.github.moco.RequestMatcher;
import com.google.common.io.CharStreams;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class ContentMatcher implements RequestMatcher {
    private String expected;

    public ContentMatcher(InputStream is) {
        try {
            this.expected = CharStreams.toString(new InputStreamReader(is));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean match(HttpRequest request) {
        return expected.equals(requestContent(request));
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
