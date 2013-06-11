package com.github.dreamhead.moco.handler.failover;

import org.jboss.netty.handler.codec.http.HttpResponse;

public interface Failover {
    void onCompleteResponse(HttpResponse response);

    Failover EMPTY_FAILOVER = new Failover() {
        @Override
        public void onCompleteResponse(HttpResponse response) {
        }
    };
}
