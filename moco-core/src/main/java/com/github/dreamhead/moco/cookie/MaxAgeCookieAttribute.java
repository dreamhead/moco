package com.github.dreamhead.moco.cookie;

import io.netty.handler.codec.http.cookie.Cookie;

import java.util.concurrent.TimeUnit;

public class MaxAgeCookieAttribute extends ActualCookieAttribute {
    private final long maxAge;
    private final TimeUnit unit;

    public MaxAgeCookieAttribute(final long maxAge, final TimeUnit unit) {
        this.maxAge = maxAge;
        this.unit = unit;
    }

    @Override
    public void visit(final Cookie cookie) {
        cookie.setMaxAge(unit.toSeconds(maxAge));
    }
}
