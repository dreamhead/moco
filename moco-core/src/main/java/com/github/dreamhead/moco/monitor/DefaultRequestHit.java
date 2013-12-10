package com.github.dreamhead.moco.monitor;

import com.github.dreamhead.moco.*;
import com.github.dreamhead.moco.model.LazyHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;

import java.util.List;

import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.Lists.newArrayList;

public class DefaultRequestHit extends AbstractMonitor implements RequestHit {
    private List<HttpRequest> unexpectedRequests = newArrayList();
    private List<HttpRequest> requests = newArrayList();

    @Override
    public void onMessageArrived(FullHttpRequest request) {
        this.requests.add(new LazyHttpRequest(request));
    }

    @Override
    public void onUnexpectedMessage(FullHttpRequest request) {
        this.unexpectedRequests.add(new LazyHttpRequest(request));
    }

    @Override
    public void verify(UnexpectedRequestMatcher matcher, VerificationMode mode) {
        mode.verify(new VerificationData(copyOf(unexpectedRequests), matcher, "expect unexpected request hit %d times but %d times"));
    }

    @Override
    public void verify(RequestMatcher matcher, VerificationMode mode) {
        mode.verify(new VerificationData(copyOf(requests), matcher, "expect request hit %d times but %d times"));
    }
}
