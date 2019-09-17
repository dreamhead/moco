package com.github.dreamhead.moco;

import java.util.Optional;

import static java.util.Optional.empty;

public abstract class HttpRequestExtractor<T> implements RequestExtractor<T> {
    protected abstract Optional<T> doExtract(HttpRequest request);

    public final Optional<T> extract(final Request request) {
        if (HttpRequest.class.isInstance(request)) {
            return doExtract(HttpRequest.class.cast(request));
        }

        return empty();
    }
}
