package com.github.dreamhead.moco.handler;

import io.netty.handler.codec.http.FullHttpRequest;

public class RequestCountingResponseStatusEvaluator implements ResponseStatusEvaluator {

    private final int numberOfRequestsBeforeSuccessfulStatus;

    private int numberOfProcessedRequests = 0;

    public RequestCountingResponseStatusEvaluator(int numberOfRequestsBeforeSuccessfulStatus) {
        this.numberOfRequestsBeforeSuccessfulStatus = numberOfRequestsBeforeSuccessfulStatus;
    }

    @Override
    public boolean shouldReturnSuccessfulStatus(FullHttpRequest request) {
        numberOfProcessedRequests++;
        return numberOfProcessedRequests > numberOfRequestsBeforeSuccessfulStatus;
    }
}
