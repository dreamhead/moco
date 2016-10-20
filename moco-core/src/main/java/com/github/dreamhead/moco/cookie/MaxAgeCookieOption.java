package com.github.dreamhead.moco.cookie;

import io.netty.handler.codec.http.cookie.Cookie;

public class MaxAgeCookieOption extends ActualCookieOption {
    private final long maxAge;

    public MaxAgeCookieOption(long maxAge) {
        this.maxAge = maxAge;
    }

    @Override
    public void visit(final Cookie cookie) {
        cookie.setMaxAge(maxAge);
    }
}
