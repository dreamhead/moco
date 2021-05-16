package com.github.dreamhead.moco.resource.reader;

import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.RequestExtractor;

import java.util.Optional;

public final class ExtractorVariable<T> implements Variable {
    private final RequestExtractor<T> extractor;

    public ExtractorVariable(final RequestExtractor<T> extractor) {
        this.extractor = extractor;
    }

    @Override
    public Object toTemplateVariable(final Request request) {
        Optional<T> extractContent = extractor.extract(request);
        return extractContent.map(this::asVariable).orElse(null);
    }

    private Object asVariable(final T target) {
        if (target instanceof String[]) {
            return target;
        }

        return target.toString();
    }
}
