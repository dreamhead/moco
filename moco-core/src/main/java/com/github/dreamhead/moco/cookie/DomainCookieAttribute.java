package com.github.dreamhead.moco.cookie;

import io.netty.handler.codec.http.cookie.Cookie;

public class DomainCookieAttribute extends ActualCookieAttribute {
    private final String domain;

    public DomainCookieAttribute(final String actualDomain) {
        this.domain = actualDomain;
    }


    @Override
    public final void visit(final Cookie cookie) {
        cookie.setDomain(this.domain);
    }
}
