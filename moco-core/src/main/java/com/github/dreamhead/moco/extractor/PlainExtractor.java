package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.RequestExtractor;
import com.google.common.base.Optional;

import static com.google.common.base.Optional.of;

public class PlainExtractor implements RequestExtractor<Object> {
    private final Object text;

    public PlainExtractor(final Object text) {
        this.text = text;
    }

    @Override
    public Optional<Object> extract(final Request request) {
        return of(this.text);
    }
}
