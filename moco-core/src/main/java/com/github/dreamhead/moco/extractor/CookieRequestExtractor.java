package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.RequestExtractor;
import com.github.dreamhead.moco.util.Cookies;
import io.netty.handler.codec.http.FullHttpRequest;

public class CookieRequestExtractor implements RequestExtractor<String> {
    private final Cookies cookies = new Cookies();

    private final String key;

    public CookieRequestExtractor(String key) {
        this.key = key;
    }

    @Override
    public String extract(FullHttpRequest request) {
        return cookies.decodeCookie(request.headers().get("Cookie"), key);
    }
}
