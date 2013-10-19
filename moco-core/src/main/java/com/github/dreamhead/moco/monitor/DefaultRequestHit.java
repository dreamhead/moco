package com.github.dreamhead.moco.monitor;

import com.github.dreamhead.moco.*;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

import java.util.List;

import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.Lists.newArrayList;

public class DefaultRequestHit implements RequestHit {
    private List<FullHttpRequest> unexpectedRequests = newArrayList();
    private List<FullHttpRequest> requests = newArrayList();

    @Override
    public void onMessageArrived(FullHttpRequest request) {
        this.requests.add(request);
    }

    @Override
    public void onException(Exception e) {
    }

    @Override
    public void onMessageLeave(FullHttpResponse response) {
    }

    @Override
    public void onUnexpectedMessage(FullHttpRequest request) {
        unexpectedRequests.add(request);
    }

    @Override
    public void verify(UnexpectedRequestMatcher matcher, VerificationMode mode) {

    }

    @Override
    public void verify(RequestMatcher matcher, VerificationMode mode) {
        mode.verify(new VerificationData(copyOf(requests), matcher));
    }
}
