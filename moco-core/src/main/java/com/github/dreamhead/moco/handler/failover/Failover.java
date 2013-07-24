package com.github.dreamhead.moco.handler.failover;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

public interface Failover {
    void onCompleteResponse(HttpRequest request, HttpResponse response);
    void failover(HttpRequest request, HttpResponse response);

    Failover EMPTY_FAILOVER = new Failover() {
        @Override
        public void onCompleteResponse(HttpRequest request, HttpResponse response) {
        }

        @Override
        public void failover(HttpRequest request, HttpResponse response) {
            throw new RuntimeException("no failover response found");
        }
    };
}
