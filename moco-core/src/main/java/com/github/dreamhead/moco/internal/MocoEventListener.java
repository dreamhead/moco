package com.github.dreamhead.moco.internal;

import com.google.common.base.Joiner;
import com.google.common.eventbus.Subscribe;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

public class MocoEventListener {
    private Logger logger = LoggerFactory.getLogger(MocoEventListener.class);
    private final Joiner.MapJoiner headerJoiner = Joiner.on(StringUtil.NEWLINE).withKeyValueSeparator(": ");

    @Subscribe
    public void onMessageArrived(HttpRequest request) {
        logger.info("Request received:\n\n{}\n", toMessageString(request));
    }

    @Subscribe
    public void onException(Exception e) {
        logger.error("Exception thrown", e);
    }

    private String toMessageString(HttpRequest request) {
        StringBuilder buf = new StringBuilder();
        appendProtocolLine(request, buf);
        buf.append(StringUtil.NEWLINE);
        headerJoiner.appendTo(buf, request.getHeaders());
        appendContent(request, buf);

        return buf.toString();
    }

    private void appendProtocolLine(HttpRequest request, StringBuilder buf) {
        buf.append(request.getMethod().toString());
        buf.append(' ');
        buf.append(request.getUri());
        buf.append(' ');
        buf.append(request.getProtocolVersion().getText());
    }

    private void appendContent(HttpRequest request, StringBuilder buf) {
        long contentLength = HttpHeaders.getContentLength(request, -1);
        if (contentLength > 0) {
            buf.append(StringUtil.NEWLINE);
            buf.append(StringUtil.NEWLINE);
            buf.append(request.getContent().toString(Charset.defaultCharset()));
        }
    }

}
