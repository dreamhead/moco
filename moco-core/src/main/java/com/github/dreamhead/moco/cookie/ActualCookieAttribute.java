package com.github.dreamhead.moco.cookie;

import com.github.dreamhead.moco.CookieAttribute;
import io.netty.handler.codec.http.cookie.Cookie;

public abstract class ActualCookieAttribute extends CookieAttribute {
    public abstract void visit(Cookie cookie);
}
