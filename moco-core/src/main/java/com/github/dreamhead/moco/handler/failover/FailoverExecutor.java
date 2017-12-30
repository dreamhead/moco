package com.github.dreamhead.moco.handler.failover;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.HttpResponse;
import com.github.dreamhead.moco.MocoException;

public interface FailoverExecutor {
    void onCompleteResponse(HttpRequest request, HttpResponse response);
    HttpResponse failover(HttpRequest request);

    FailoverExecutor EMPTY_FAILOVER = new FailoverExecutor() {
        @Override
        public void onCompleteResponse(final HttpRequest request, final HttpResponse response) {
        }

        @Override
        public HttpResponse failover(final HttpRequest request) {
            throw new MocoException("no failover response found");
        }
    };
}
