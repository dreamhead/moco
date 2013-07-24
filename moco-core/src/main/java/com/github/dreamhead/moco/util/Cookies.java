package com.github.dreamhead.moco.util;

import com.google.common.base.Predicate;
import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.CookieDecoder;
import io.netty.handler.codec.http.DefaultCookie;
import io.netty.handler.codec.http.ServerCookieEncoder;

import java.util.Set;

import static com.google.common.collect.Iterables.find;

public class Cookies {
    public String encodeCookie(String key, String value) {
        Cookie cookie = new DefaultCookie(key, value);
        cookie.setPath("/");
        return ServerCookieEncoder.encode(cookie);
    }

    public String decodeCookie(String cookieString, String key) {
        if(cookieString == null) {
            return null;
        }

        Set<Cookie> cookies = CookieDecoder.decode(cookieString);

        Cookie cookie = find(cookies, byCookieName(key), null);
        return cookie == null ? null : cookie.getValue();
    }

    private static Predicate<Cookie> byCookieName(final String key) {
        return new Predicate<Cookie>() {
            @Override
            public boolean apply(Cookie cookie) {
                return key.equals(cookie.getName());
            }
        };
    }
}
