package com.github.dreamhead.moco.cookie;

import com.github.dreamhead.moco.CookieOption;
import io.netty.handler.codec.http.cookie.Cookie;

public abstract class ActualCookieOption extends CookieOption {
    public abstract void visit(final Cookie cookie);
}
