package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.RequestExtractor;
import com.google.common.base.Optional;

import static com.google.common.base.Optional.of;

public class PlainExtractor<T> implements RequestExtractor<T> {
    private final T text;

    public PlainExtractor(final T text) {
        this.text = text;
    }

    @Override
    public Optional<T> extract(final Request request) {
        return of(this.text);
    }
}
