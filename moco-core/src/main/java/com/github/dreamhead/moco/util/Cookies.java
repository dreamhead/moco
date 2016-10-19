package com.github.dreamhead.moco.util;

import com.github.dreamhead.moco.CookieOption;
import com.github.dreamhead.moco.cookie.ActualCookieOption;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;

public final class Cookies {
    public String encodeCookie(final String key, final String value, final CookieOption... options) {
        Cookie cookie = new DefaultCookie(key, value);
        for (CookieOption option : options) {
            ((ActualCookieOption)option).visit(cookie);
        }
        return ServerCookieEncoder.STRICT.encode(cookie);
    }
}
