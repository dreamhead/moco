package com.github.dreamhead.moco;

import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

public interface ResponseHandler {
    void writeToResponse(HttpRequest request, HttpResponse response);

    void apply(MocoConfig config);
}
