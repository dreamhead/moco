package com.github.moco.request;

import com.github.moco.MocoServer;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;

import java.nio.charset.Charset;

public class ContentRequestSetting extends BaseRequestSetting {
    private String content;

    public ContentRequestSetting(MocoServer server, String content) {
        super(server);
        this.content = content;
    }

    public boolean match(HttpRequest request) {
        long contentLength = HttpHeaders.getContentLength(request);
        ChannelBuffer contentBuffer = request.getContent();
        String content = contentBuffer.toString((int) (contentBuffer.capacity() - contentLength), (int) contentLength, Charset.defaultCharset());

        return content.equals(this.content);
    }
}
