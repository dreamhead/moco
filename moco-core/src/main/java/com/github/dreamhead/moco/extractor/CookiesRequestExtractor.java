package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.HttpRequestExtractor;
import com.github.dreamhead.moco.RequestExtractor;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static com.github.dreamhead.moco.util.HttpHeaders.COOKIE;
import static com.github.dreamhead.moco.util.Maps.toOrderedMap;

public class CookiesRequestExtractor extends HttpRequestExtractor<Map<String, String>> {
    private final RequestExtractor<String[]> extractor = new HeaderRequestExtractor(COOKIE);

    @Override
    protected final Optional<Map<String, String>> doExtract(final HttpRequest request) {
        Optional<String[]> cookieString = extractor.extract(request);
        return cookieString.map(CookiesRequestExtractor::doExtractCookies);
    }

    private static Map<String, String> doExtractCookies(final String[] cookieStrings) {
        return Arrays.stream(cookieStrings)
                .flatMap(cookie -> ServerCookieDecoder.STRICT.decode(cookie).stream())
                .collect(toOrderedMap(Cookie::name, Cookie::value));
    }
}
