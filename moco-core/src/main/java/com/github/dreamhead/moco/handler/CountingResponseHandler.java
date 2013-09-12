package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.ResponseHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

public class CountingResponseHandler implements ResponseHandler {

    private final int numberOfAttemptsBeforeSuccessfulResponse;

    static HttpResponseStatus failureResponseStatus = HttpResponseStatus.BAD_REQUEST;

    static HttpResponseStatus successResponseStatus = HttpResponseStatus.OK;

    private int numberOfRequestsAlreadyServed = 0;

    public CountingResponseHandler(int numberOfAttemptBeforeSuccessfulResponse) {
        this.numberOfAttemptsBeforeSuccessfulResponse = numberOfAttemptBeforeSuccessfulResponse;
    }

    @Override
    public ResponseHandler apply(MocoConfig config) {
        return this;
    }

    @Override
    public void writeToResponse(FullHttpRequest request, FullHttpResponse response) {
        numberOfRequestsAlreadyServed++;
        if (numberOfRequestsAlreadyServed > numberOfAttemptsBeforeSuccessfulResponse) {
            response.setStatus(successResponseStatus);
        } else {
            response.setStatus(failureResponseStatus);
        }
    }

    public CountingResponseHandler withSuccessStatus(HttpResponseStatus customSuccessStatus) {
        this.successResponseStatus = customSuccessStatus;
        return this;
    }

    public CountingResponseHandler withFailureStatus(HttpResponseStatus customFailureStatus) {
        this.failureResponseStatus = customFailureStatus;
        return this;
    }
}
