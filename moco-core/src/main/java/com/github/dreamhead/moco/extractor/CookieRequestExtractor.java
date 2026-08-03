package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.HttpRequestExtractor;

import java.util.Map;
import java.util.Optional;

public final class CookieRequestExtractor extends HttpRequestExtractor<String> {
    private final CookiesRequestExtractor extractor = new CookiesRequestExtractor();

    private final String key;

    public CookieRequestExtractor(final String key) {
        this.key = key;
    }

    @Override
    protected Optional<String> doExtract(final HttpRequest request) {
        Optional<Map<String, String>> cookies = extractor.extract(request);
        return cookies.map(cookie -> cookie.get(this.key));
    }
}
