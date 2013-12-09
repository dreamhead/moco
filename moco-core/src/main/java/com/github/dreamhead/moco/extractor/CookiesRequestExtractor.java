package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.RequestExtractor;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.CookieDecoder;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;

import java.util.Map;
import java.util.Set;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;
import static com.google.common.collect.ImmutableMap.copyOf;
import static com.google.common.collect.Maps.newHashMap;

public class CookiesRequestExtractor implements RequestExtractor<ImmutableMap<String, String>> {
    public Optional<ImmutableMap<String, String>> extract(FullHttpRequest request) {
        String cookieString = request.headers().get(HttpHeaders.Names.COOKIE);
        if (cookieString == null) {
            return absent() ;
        }

        return of(doExtract(cookieString));
    }

    private static ImmutableMap<String, String> doExtract(String cookieString) {
        Set<Cookie> cookies = CookieDecoder.decode(cookieString);
        Map<String, String> target = newHashMap();
        for (Cookie cookie : cookies) {
            target.put(cookie.getName(), cookie.getValue());
        }

        return copyOf(target);
    }
}
