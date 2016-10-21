package com.github.dreamhead.moco.cookie;

import io.netty.handler.codec.http.cookie.Cookie;

public class SecureCookieOption extends ActualCookieOption {
    private final boolean secure;

    public SecureCookieOption(final boolean secure) {
        this.secure = secure;
    }

    @Override
    public void visit(final Cookie cookie) {
        cookie.setSecure(this.secure);
    }
}
