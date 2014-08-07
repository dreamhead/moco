package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.HttpRequestExtractor;
import com.github.dreamhead.moco.RequestExtractor;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.CookieDecoder;
import io.netty.handler.codec.http.HttpHeaders;

import java.util.Map;
import java.util.Set;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;
import static com.google.common.collect.ImmutableMap.copyOf;
import static com.google.common.collect.Maps.newHashMap;

public class CookiesRequestExtractor extends HttpRequestExtractor<ImmutableMap<String, String>> {
    private final RequestExtractor<String> extractor = new HeaderRequestExtractor(HttpHeaders.Names.COOKIE);

    @Override
    protected Optional<ImmutableMap<String, String>> doExtract(final HttpRequest request) {
        Optional<String> cookieString = extractor.extract(request);
        if (!cookieString.isPresent()) {
            return absent() ;
        }

        return of(doExtractCookies(cookieString.get()));
    }

    private static ImmutableMap<String, String> doExtractCookies(String cookieString) {
        Set<Cookie> cookies = CookieDecoder.decode(cookieString);
        Map<String, String> target = newHashMap();
        for (Cookie cookie : cookies) {
            target.put(cookie.getName(), cookie.getValue());
        }

        return copyOf(target);
    }
}
