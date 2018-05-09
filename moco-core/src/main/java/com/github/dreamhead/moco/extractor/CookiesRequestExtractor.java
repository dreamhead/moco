package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.HttpRequestExtractor;
import com.github.dreamhead.moco.RequestExtractor;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;

import java.util.Set;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;
import static com.google.common.net.HttpHeaders.COOKIE;

public class CookiesRequestExtractor extends HttpRequestExtractor<ImmutableMap<String, String>> {
    private final RequestExtractor<String[]> extractor = new HeaderRequestExtractor(COOKIE);

    @Override
    protected final Optional<ImmutableMap<String, String>> doExtract(final HttpRequest request) {
        Optional<String[]> cookieString = extractor.extract(request);
        if (!cookieString.isPresent()) {
            return absent();
        }

        return of(doExtractCookies(cookieString.get()));
    }

    private static ImmutableMap<String, String> doExtractCookies(final String[] cookieStrings) {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();

        for (String cookie : cookieStrings) {
            Set<Cookie> decodeCookies = ServerCookieDecoder.STRICT.decode(cookie);
            for (Cookie decodeCookie : decodeCookies) {
                builder.put(decodeCookie.name(), decodeCookie.value());
            }
        }

        return builder.build();
    }
}
