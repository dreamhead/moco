package com.github.dreamhead.moco.cookie;

import io.netty.handler.codec.http.cookie.Cookie;

public class PathCookieAttribute extends ActualCookieAttribute {
    private final String path;

    public PathCookieAttribute(final String path) {
        this.path = path;
    }

    @Override
    public void visit(final Cookie cookie) {
        cookie.setPath(path);
    }
}
