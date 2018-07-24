package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.HttpRequestExtractor;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

import static com.google.common.base.Optional.fromNullable;

public final class CookieRequestExtractor extends HttpRequestExtractor<String> {
    private final CookiesRequestExtractor extractor = new CookiesRequestExtractor();

    private final String key;

    public CookieRequestExtractor(final String key) {
        this.key = key;
    }

    @Override
    protected Optional<String> doExtract(final HttpRequest request) {
        Optional<ImmutableMap<String, String>> cookies = extractor.extract(request);
        if (cookies.isPresent()) {
            return fromNullable(cookies.get().get(this.key));
        }

        return Optional.absent();
    }
}
