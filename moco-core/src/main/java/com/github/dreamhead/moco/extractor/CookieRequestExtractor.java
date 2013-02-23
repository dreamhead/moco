package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.RequestExtractor;
import com.google.common.base.Predicate;
import org.jboss.netty.handler.codec.http.Cookie;
import org.jboss.netty.handler.codec.http.CookieDecoder;
import org.jboss.netty.handler.codec.http.HttpRequest;

import java.util.Set;

import static com.google.common.collect.Iterables.find;

public class CookieRequestExtractor implements RequestExtractor {
    private CookieDecoder decoder = new CookieDecoder();

    private final String key;

    public CookieRequestExtractor(String key) {
        this.key = key;
    }

    @Override
    public String extract(HttpRequest request) {
        String cookieString = request.getHeader("Cookie");
        if(cookieString == null) {
            return null;
        }

        Set<Cookie> cookies = decoder.decode(cookieString);

        Cookie cookie = find(cookies, byCookieName(key), null);
        return cookie == null ? null : cookie.getValue();
    }

    private Predicate<Cookie> byCookieName(final String key) {
        return new Predicate<Cookie>() {
            @Override
            public boolean apply(Cookie cookie) {
                return key.equals(cookie.getName());
            }
        };
    }
}
