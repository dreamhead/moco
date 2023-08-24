package com.github.dreamhead.moco.cookie;

import io.netty.handler.codec.http.cookie.DefaultCookie;

public class PathCookieAttribute extends ActualCookieAttribute {
    private final String path;

    public PathCookieAttribute(final String actualPath) {
        this.path = actualPath;
    }

    @Override
    public final void visit(final DefaultCookie cookie) {
        cookie.setPath(path);
    }
}
