package com.github.dreamhead.moco.util;

import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;


public class Cookies {
    public String encodeCookie(String key, String value) {
        Cookie cookie = new DefaultCookie(key, value);
        cookie.setPath("/");
        return ServerCookieEncoder.STRICT.encode(cookie);
    }
}
