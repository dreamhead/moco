package com.github.dreamhead.moco.monitor;

import com.github.dreamhead.moco.Request;
import io.netty.handler.codec.http.FullHttpResponse;

public interface LogFormatter {
    String format(final Request request);
    String format(final FullHttpResponse response);
    String format(final Exception e);
}
