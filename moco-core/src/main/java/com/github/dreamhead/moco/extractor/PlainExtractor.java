package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.RequestExtractor;
import com.google.common.base.Optional;

import static com.google.common.base.Optional.of;

public final class PlainExtractor<T> implements RequestExtractor<T> {
    private final T object;

    public PlainExtractor(final T object) {
        this.object = object;
    }

    @Override
    public java.util.Optional<T> extract(final Request request) {
        return java.util.Optional.of(this.object);
    }
}
