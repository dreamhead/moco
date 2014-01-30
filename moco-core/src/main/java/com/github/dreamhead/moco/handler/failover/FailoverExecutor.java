package com.github.dreamhead.moco.handler.failover;

import com.github.dreamhead.moco.HttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public interface FailoverExecutor {
    void onCompleteResponse(HttpRequest request, FullHttpResponse response);
    void failover(HttpRequest request, FullHttpResponse response);

    FailoverExecutor EMPTY_FAILOVER = new FailoverExecutor() {
        @Override
        public void onCompleteResponse(HttpRequest request, FullHttpResponse response) {
        }

        @Override
        public void failover(HttpRequest request, FullHttpResponse response) {
            throw new RuntimeException("no failover response found");
        }
    };
}
