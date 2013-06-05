package com.github.dreamhead.moco.util;

import com.google.common.base.Predicate;
import org.jboss.netty.handler.codec.http.Cookie;
import org.jboss.netty.handler.codec.http.CookieDecoder;
import org.jboss.netty.handler.codec.http.CookieEncoder;
import org.jboss.netty.handler.codec.http.DefaultCookie;

import java.util.Set;

import static com.google.common.collect.Iterables.find;

public class Cookies {
    private final CookieDecoder decoder = new CookieDecoder();

    public String encodeCookie(String key, String value) {
        CookieEncoder cookieEncoder = new CookieEncoder(true);
        DefaultCookie cookie = new DefaultCookie(key, value);
        cookie.setPath("/");
        cookieEncoder.addCookie(cookie);

        return cookieEncoder.encode();
    }

    public String decodeCookie(String cookieString, String key) {
        if(cookieString == null) {
            return null;
        }

        Set<Cookie> cookies = decoder.decode(cookieString);

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
