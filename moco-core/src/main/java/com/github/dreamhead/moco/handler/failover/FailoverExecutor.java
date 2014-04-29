package com.github.dreamhead.moco.handler.failover;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.HttpResponse;

public interface FailoverExecutor {
    void onCompleteResponse(HttpRequest request, HttpResponse response);
    HttpResponse failover(HttpRequest request);

    FailoverExecutor EMPTY_FAILOVER = new FailoverExecutor() {
        @Override
        public void onCompleteResponse(HttpRequest request, HttpResponse response) {
        }

        @Override
        public HttpResponse failover(HttpRequest request) {
            throw new RuntimeException("no failover response found");
        }
    };
}
