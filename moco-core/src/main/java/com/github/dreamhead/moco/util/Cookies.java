package com.github.dreamhead.moco.util;

import com.github.dreamhead.moco.CookieAttribute;
import com.github.dreamhead.moco.cookie.ActualCookieAttribute;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;

public final class Cookies {
    public String encodeCookie(final String key, final String value, final CookieAttribute... options) {
        Cookie cookie = new DefaultCookie(key, value);
        for (CookieAttribute option : options) {
            ((ActualCookieAttribute) option).visit(cookie);
        }
        return ServerCookieEncoder.STRICT.encode(cookie);
    }
}
