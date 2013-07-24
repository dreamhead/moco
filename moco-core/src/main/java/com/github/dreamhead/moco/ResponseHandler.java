package com.github.dreamhead.moco;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

public interface ResponseHandler extends ConfigApplier<ResponseHandler> {
    void writeToResponse(HttpRequest request, HttpResponse response);
}
