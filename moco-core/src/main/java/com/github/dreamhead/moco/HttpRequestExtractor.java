package com.github.dreamhead.moco;

import com.google.common.base.Optional;

public abstract class HttpRequestExtractor<T> implements RequestExtractor<T> {
    protected abstract Optional<T> doExtract(final HttpRequest request);

    public Optional<T> extract(final HttpRequest request) {
        return doExtract(request);
    }
}
