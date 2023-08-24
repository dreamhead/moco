package com.github.dreamhead.moco.cookie;

import io.netty.handler.codec.http.cookie.DefaultCookie;

public final class DomainCookieAttribute extends ActualCookieAttribute {
    private final String domain;

    public DomainCookieAttribute(final String actualDomain) {
        this.domain = actualDomain;
    }


    @Override
    public void visit(final DefaultCookie cookie) {
        cookie.setDomain(this.domain);
    }
}
