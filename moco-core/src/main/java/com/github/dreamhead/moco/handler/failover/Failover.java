package com.github.dreamhead.moco.handler.failover;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public interface Failover {
    void onCompleteResponse(FullHttpRequest request, FullHttpResponse response);
    void failover(FullHttpRequest request, FullHttpResponse response);

    Failover EMPTY_FAILOVER = new Failover() {
        @Override
        public void onCompleteResponse(FullHttpRequest request, FullHttpResponse response) {
        }

        @Override
        public void failover(FullHttpRequest request, FullHttpResponse response) {
            throw new RuntimeException("no failover response found");
        }
    };
}
