package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.RequestExtractor;
import com.google.common.base.Optional;

import static com.google.common.base.Optional.of;

public class PlainTextExtractor implements RequestExtractor<String> {
    private final String text;

    public PlainTextExtractor(final String text) {
        this.text = text;
    }

    @Override
    public Optional<String> extract(final Request request) {
        return of(this.text);
    }
}
