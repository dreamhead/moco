package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.ResponseHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

public class ConditionalResponseStatusHandler implements ResponseHandler {

    static final HttpResponseStatus DEFAULT_FAILURE_STATUS = HttpResponseStatus.BAD_REQUEST;

    static final HttpResponseStatus DEFAULT_SUCCESS_STATUS = HttpResponseStatus.OK;

    HttpResponseStatus failureStatus = DEFAULT_FAILURE_STATUS;
    HttpResponseStatus successStatus = DEFAULT_SUCCESS_STATUS;

    final ResponseStatusEvaluator responseStatusEvaluator;

    public ConditionalResponseStatusHandler(ResponseStatusEvaluator responseStatusEvaluator) {
        this.responseStatusEvaluator = responseStatusEvaluator;
    }

    @Override
    public ResponseHandler apply(MocoConfig config) {
        return this;
    }

    @Override
    public void writeToResponse(FullHttpRequest request, FullHttpResponse response) {
        if (responseStatusEvaluator.shouldReturnSuccessfulStatus(request)) {
            response.setStatus(successStatus);
        } else {
            response.setStatus(failureStatus);
        }
    }

    public ConditionalResponseStatusHandler withSuccessStatus(HttpResponseStatus customSuccessStatus) {
        this.successStatus = customSuccessStatus;
        return this;
    }

    public ConditionalResponseStatusHandler withFailureStatus(HttpResponseStatus customFailureStatus) {
        this.failureStatus = customFailureStatus;
        return this;
    }
}
