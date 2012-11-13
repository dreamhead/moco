package com.github.dreamhead.moco;

import org.jboss.netty.handler.codec.http.HttpResponse;

public interface ResponseHandler {
    HttpResponse createResponse();
}
