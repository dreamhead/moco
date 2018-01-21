package com.github.dreamhead.moco.cookie;

import io.netty.handler.codec.http.cookie.Cookie;

import java.util.concurrent.TimeUnit;

public final class MaxAgeCookieAttribute extends ActualCookieAttribute {
    private final long maxAge;
    private final TimeUnit unit;

    public MaxAgeCookieAttribute(final long actualMaxAge, final TimeUnit actualUnit) {
        this.maxAge = actualMaxAge;
        this.unit = actualUnit;
    }

    @Override
    public void visit(final Cookie cookie) {
        cookie.setMaxAge(unit.toSeconds(maxAge));
    }
}
