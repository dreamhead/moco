package com.github.dreamhead.moco.cookie;

import io.netty.handler.codec.http.cookie.Cookie;

public class PathCookieOption extends ActualCookieOption {
    private final String path;

    public PathCookieOption(final String path) {
        this.path = path;
    }

    @Override
    public void visit(Cookie cookie) {
        cookie.setPath(path);
    }
}
