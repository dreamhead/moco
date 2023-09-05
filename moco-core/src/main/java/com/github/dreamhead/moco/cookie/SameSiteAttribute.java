package com.github.dreamhead.moco.cookie;

import io.netty.handler.codec.http.cookie.CookieHeaderNames;
import io.netty.handler.codec.http.cookie.DefaultCookie;

public final class SameSiteAttribute extends ActualCookieAttribute {
    private final CookieHeaderNames.SameSite sameSite;

    public SameSiteAttribute(final CookieHeaderNames.SameSite sameSite) {
        this.sameSite = sameSite;
    }

    @Override
    public void visit(final DefaultCookie cookie) {
        cookie.setSameSite(sameSite);
    }
}
