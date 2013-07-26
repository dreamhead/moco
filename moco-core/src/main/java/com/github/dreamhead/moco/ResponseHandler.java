package com.github.dreamhead.moco;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public interface ResponseHandler extends ConfigApplier<ResponseHandler> {
    void writeToResponse(FullHttpRequest request, FullHttpResponse response);
}
