package com.github.dreamhead.moco.cookie;

import io.netty.handler.codec.http.cookie.DefaultCookie;

public final class HttpOnlyAttribute extends ActualCookieAttribute {
    @Override
    public void visit(final DefaultCookie cookie) {
        cookie.setHttpOnly(true);
    }
}
