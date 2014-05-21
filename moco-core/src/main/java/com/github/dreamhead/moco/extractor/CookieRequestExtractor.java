package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.RequestExtractor;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

import static com.google.common.base.Optional.fromNullable;

public class CookieRequestExtractor implements RequestExtractor<String> {
    private final CookiesRequestExtractor extractor = new CookiesRequestExtractor();

    private final String key;

    public CookieRequestExtractor(final String key) {
        this.key = key;
    }

    @Override
    public Optional<String> extract(final HttpRequest request) {
        Optional<ImmutableMap<String,String>> cookies = extractor.extract(request);
        return cookies.isPresent() ? fromNullable(cookies.get().get(this.key)) : Optional.<String>absent();
    }
}
