package com.github.dreamhead.moco.cookie;

import io.netty.handler.codec.http.cookie.Cookie;

public class HttpOnlyOption extends ActualCookieOption {
    @Override
    public void visit(final Cookie cookie) {
        cookie.setHttpOnly(true);
    }
}
