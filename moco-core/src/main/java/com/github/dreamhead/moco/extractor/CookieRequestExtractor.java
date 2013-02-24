package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.RequestExtractor;
import com.github.dreamhead.moco.util.Cookies;
import org.jboss.netty.handler.codec.http.HttpRequest;

public class CookieRequestExtractor implements RequestExtractor {
    private final String key;
    private Cookies cookies;

    public CookieRequestExtractor(String key) {
        this.key = key;
    }

    @Override
    public String extract(HttpRequest request) {
        cookies = new Cookies();
        return cookies.decodeCookie(request.getHeader("Cookie"), key);
    }
}
