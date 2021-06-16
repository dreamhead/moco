package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.HttpRequestExtractor;
import com.github.dreamhead.moco.RequestExtractor;
import com.google.common.collect.ImmutableMap;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;

import java.util.Arrays;
import java.util.Optional;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static com.google.common.net.HttpHeaders.COOKIE;

public class CookiesRequestExtractor extends HttpRequestExtractor<ImmutableMap<String, String>> {
    private final RequestExtractor<String[]> extractor = new HeaderRequestExtractor(COOKIE);

    @Override
    protected final Optional<ImmutableMap<String, String>> doExtract(final HttpRequest request) {
        Optional<String[]> cookieString = extractor.extract(request);
        return cookieString.map(CookiesRequestExtractor::doExtractCookies);
    }

    private static ImmutableMap<String, String> doExtractCookies(final String[] cookieStrings) {
        return Arrays.stream(cookieStrings)
                .flatMap(cookie -> ServerCookieDecoder.STRICT.decode(cookie).stream())
                .collect(toImmutableMap(Cookie::name, Cookie::value));
    }
}
