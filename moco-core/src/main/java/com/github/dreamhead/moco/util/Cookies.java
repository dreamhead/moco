package com.github.dreamhead.moco.util;

import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.DefaultCookie;
import io.netty.handler.codec.http.ServerCookieEncoder;

public class Cookies {
    public String encodeCookie(String key, String value) {
        Cookie cookie = new DefaultCookie(key, value);
        cookie.setPath("/");
        return ServerCookieEncoder.encode(cookie);
    }
}
