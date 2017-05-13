package com.github.dreamhead.moco.handler;

public class ResponseStatusEvaluators {
    public static ResponseStatusEvaluator numberOfRequestsEvaluator(int numberOfRequestsBeforeSuccessfulStatus) {
        return new RequestCountingResponseStatusEvaluator(numberOfRequestsBeforeSuccessfulStatus);
    }
}
