package com.github.dreamhead.moco.resource.reader;

import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.RequestExtractor;
import com.google.common.base.Optional;

public final class ExtractorVariable<T> implements Variable {
    private final RequestExtractor<T> extractor;

    public ExtractorVariable(final RequestExtractor<T> extractor) {
        this.extractor = extractor;
    }

    @Override
    public Object toTemplateVariable(final Request request) {
        Optional<T> extractContent = extractor.extract(request);
        if (!extractContent.isPresent()) {
            return null;
        }

        T target = extractContent.get();
        if (target instanceof String[]) {
            return target;
        }

        return target.toString();
    }
}
