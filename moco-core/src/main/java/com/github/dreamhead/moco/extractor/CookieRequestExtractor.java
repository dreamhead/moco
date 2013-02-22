package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.RequestExtractor;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import org.jboss.netty.handler.codec.http.Cookie;
import org.jboss.netty.handler.codec.http.CookieDecoder;
import org.jboss.netty.handler.codec.http.HttpRequest;

import java.util.Collection;
import java.util.Set;

public class CookieRequestExtractor implements RequestExtractor {
    private String key;

    public CookieRequestExtractor(String key) {
        this.key = key;
    }

    @Override
    public String extract(HttpRequest request) {
        String cookieString = request.getHeader("Cookie");
        if(cookieString == null) {
            return null;
        }

        Set<Cookie> cookies = new CookieDecoder().decode(cookieString);
        Collection<Cookie> cookiesWithGivenKey = Collections2.filter(cookies, new Predicate<Cookie>() {
            @Override
            public boolean apply(Cookie cookie) {
                return cookie.getName().equals(key);
            }
        });
        if(cookiesWithGivenKey.isEmpty()) {
            return null;
        }

        Cookie cookie = cookiesWithGivenKey.iterator().next();
        return cookie.getValue();
    }
}
