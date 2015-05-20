package com.github.dreamhead.moco.handler.failover;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.HttpResponse;

public interface FailoverExecutor {
    void onCompleteResponse(final HttpRequest request, final HttpResponse response);
    HttpResponse failover(final HttpRequest request);

    FailoverExecutor EMPTY_FAILOVER = new FailoverExecutor() {
        @Override
        public void onCompleteResponse(final HttpRequest request, final HttpResponse response) {
        }

        @Override
        public HttpResponse failover(final HttpRequest request) {
            throw new RuntimeException("no failover response found");
        }
    };
}
