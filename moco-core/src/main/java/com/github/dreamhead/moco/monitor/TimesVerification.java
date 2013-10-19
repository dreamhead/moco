package com.github.dreamhead.moco.monitor;

import com.github.dreamhead.moco.VerificationData;
import com.github.dreamhead.moco.VerificationException;
import com.github.dreamhead.moco.VerificationMode;
import com.google.common.base.Predicate;
import io.netty.handler.codec.http.FullHttpRequest;

import static com.google.common.collect.FluentIterable.from;
import static java.lang.String.format;

public class TimesVerification implements VerificationMode {
    private final int count;

    public TimesVerification(int count) {
        this.count = count;
    }

    @Override
    public void verify(final VerificationData data) {
        int size = from(data.getRequests()).filter(matched(data)).size();
        if (size != count) {
            throw new VerificationException(format("expect request hit %d times but %d times", count, size));
        }
    }

    private Predicate<FullHttpRequest> matched(final VerificationData data) {
        return new Predicate<FullHttpRequest>() {
            @Override
            public boolean apply(FullHttpRequest request) {
                return data.getMatcher().match(request);
            }
        };
    }
}
