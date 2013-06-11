package com.github.dreamhead.moco.dumper;

import com.google.common.base.Joiner;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMessage;
import org.jboss.netty.util.internal.StringUtil;

import java.nio.charset.Charset;

public abstract class HttpMessageBaseDumper<T> implements Dumper<T> {
    protected final Joiner.MapJoiner headerJoiner = Joiner.on(StringUtil.NEWLINE).withKeyValueSeparator(": ");

    protected static void appendContent(HttpMessage request, StringBuilder buf) {
        long contentLength = HttpHeaders.getContentLength(request, -1);
        if (contentLength > 0) {
            buf.append(StringUtil.NEWLINE);
            buf.append(StringUtil.NEWLINE);
            buf.append(request.getContent().toString(Charset.defaultCharset()));
        }
    }
}
