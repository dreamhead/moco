package com.github.dreamhead.moco.monitor;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public interface LogFormatter {
    String format(FullHttpRequest request);
    String format(FullHttpResponse response);
    String format(Exception e);
}
