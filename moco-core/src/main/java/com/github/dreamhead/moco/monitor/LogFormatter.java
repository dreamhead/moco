package com.github.dreamhead.moco.monitor;

import com.github.dreamhead.moco.HttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public interface LogFormatter {
    String format(HttpRequest request);
    String format(FullHttpResponse response);
    String format(Exception e);
}
