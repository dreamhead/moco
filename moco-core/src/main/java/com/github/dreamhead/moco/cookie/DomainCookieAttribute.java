package com.github.dreamhead.moco.cookie;

import io.netty.handler.codec.http.cookie.Cookie;

public class DomainCookieAttribute extends ActualCookieAttribute {
    private final String domain;

    public DomainCookieAttribute(final String domain) {
        this.domain = domain;
    }


    @Override
    public void visit(final Cookie cookie) {
        cookie.setDomain(this.domain);
    }
}
