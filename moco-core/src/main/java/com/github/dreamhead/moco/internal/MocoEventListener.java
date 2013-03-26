package com.github.dreamhead.moco.internal;

import com.google.common.base.Joiner;
import com.google.common.eventbus.Subscribe;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMessage;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

public class MocoEventListener {
    private Logger logger = LoggerFactory.getLogger(MocoEventListener.class);
    private final Joiner.MapJoiner headerJoiner = Joiner.on(StringUtil.NEWLINE).withKeyValueSeparator(": ");

    @Subscribe
    public void onMessageArrived(HttpRequest request) {
        logger.info("Request received:\n\n{}\n", toRequestString(request));
    }

    @Subscribe
    public void onException(Exception e) {
        logger.error("Exception thrown", e);
    }

    @Subscribe
    public void onMessageLeave(HttpResponse response) {
        logger.info("Response return:\n\n{}\n", toResponseString(response));
    }

    private String toRequestString(HttpRequest request) {
        StringBuilder buf = new StringBuilder();
        appendRequestProtocolLine(request, buf);
        buf.append(StringUtil.NEWLINE);
        headerJoiner.appendTo(buf, request.getHeaders());
        appendContent(request, buf);

        return buf.toString();
    }

    private String toResponseString(HttpResponse response) {
//        return response.toString();

        StringBuilder buf = new StringBuilder();
        appendResponseProtocolLine(response, buf);
        buf.append(StringUtil.NEWLINE);
        headerJoiner.appendTo(buf, response.getHeaders());
        appendContent(response, buf);

        return buf.toString();
    }

    private void appendResponseProtocolLine(HttpResponse response, StringBuilder buf) {
        buf.append(response.getProtocolVersion().getText());
        buf.append(' ');
        buf.append(response.getStatus().toString());
    }

    private void appendRequestProtocolLine(HttpRequest request, StringBuilder buf) {
        buf.append(request.getMethod().toString());
        buf.append(' ');
        buf.append(request.getUri());
        buf.append(' ');
        buf.append(request.getProtocolVersion().getText());
    }

    private void appendContent(HttpMessage request, StringBuilder buf) {
        long contentLength = HttpHeaders.getContentLength(request, -1);
        if (contentLength > 0) {
            buf.append(StringUtil.NEWLINE);
            buf.append(StringUtil.NEWLINE);
            buf.append(request.getContent().toString(Charset.defaultCharset()));
        }
    }
}
