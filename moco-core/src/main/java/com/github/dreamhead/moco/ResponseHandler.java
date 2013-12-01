package com.github.dreamhead.moco;

import com.github.dreamhead.moco.internal.SessionContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public interface ResponseHandler extends ConfigApplier<ResponseHandler> {
    void writeToResponse(SessionContext context);
}
