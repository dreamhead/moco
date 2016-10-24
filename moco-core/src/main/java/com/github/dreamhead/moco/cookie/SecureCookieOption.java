package com.github.dreamhead.moco.cookie;

import io.netty.handler.codec.http.cookie.Cookie;

public class SecureCookieOption extends ActualCookieOption {
    @Override
    public void visit(final Cookie cookie) {
        cookie.setSecure(true);
    }
}
