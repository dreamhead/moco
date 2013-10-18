package com.github.dreamhead.moco.monitor;

import com.github.dreamhead.moco.RequestHit;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.UnexpectedRequestMatcher;
import com.github.dreamhead.moco.VerificationMode;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public class DefaultRequestHit implements RequestHit {
    @Override
    public void onMessageArrived(FullHttpRequest request) {
    }

    @Override
    public void onException(Exception e) {
    }

    @Override
    public void onMessageLeave(FullHttpResponse response) {
    }

    @Override
    public void verify(UnexpectedRequestMatcher matcher, VerificationMode mode) {
    }
}
