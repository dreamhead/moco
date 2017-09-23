package com.github.dreamhead.moco.monitor;

import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.RequestHit;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.UnexpectedRequestMatcher;
import com.github.dreamhead.moco.VerificationData;
import com.github.dreamhead.moco.VerificationMode;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

public final class DefaultRequestHit extends AbstractMonitor implements RequestHit {
    private List<Request> unexpectedRequests = newArrayList();
    private List<Request> requests = newArrayList();

    @Override
    public void onMessageArrived(final Request request) {
        this.requests.add(request);
    }

    @Override
    public void onUnexpectedMessage(final Request request) {
        this.unexpectedRequests.add(request);
    }

    @Override
    public void verify(final UnexpectedRequestMatcher matcher, final VerificationMode mode) {
        checkNotNull(mode, "Verification mode should not be null")
                .verify(new VerificationData(unexpectedRequests,
                        checkNotNull(matcher, "Matcher should not be null"),
                        "expect unexpected request hit %s times but %d times"));
    }

    @Override
    public void verify(final RequestMatcher matcher, final VerificationMode mode) {
        checkNotNull(mode, "Verification mode should not be null").verify(new VerificationData(requests,
                checkNotNull(matcher, "Matcher should not be null"),
                "expect request hit %s times but %d times"));
    }
}
