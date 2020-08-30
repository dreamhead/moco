package com.github.dreamhead.moco;

import java.util.Optional;

import static java.util.Optional.empty;

public abstract class HttpRequestExtractor<T> implements RequestExtractor<T> {
    protected abstract Optional<T> doExtract(HttpRequest request);

    public final Optional<T> extract(final Request request) {
        if (request instanceof HttpRequest) {
            return doExtract((HttpRequest) request);
        }

        return empty();
    }
}
