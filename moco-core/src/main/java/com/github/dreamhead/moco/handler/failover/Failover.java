package com.github.dreamhead.moco.handler.failover;

import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

public interface Failover {
    void onCompleteResponse(HttpRequest request, HttpResponse response);
    void failover(HttpResponse response);

    Failover EMPTY_FAILOVER = new Failover() {
        @Override
        public void onCompleteResponse(HttpRequest request, HttpResponse response) {
        }

        @Override
        public void failover(HttpResponse response) {
        }
    };
}
