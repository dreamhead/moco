package com.github.dreamhead.moco.resource.reader;

import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.RequestExtractor;
import com.google.common.base.Optional;

public class ExtractorVariable<T> implements Variable {
    private final RequestExtractor<T> extractor;

    public ExtractorVariable(RequestExtractor<T> extractor) {
        this.extractor = extractor;
    }

    @Override
    public String toString(Request request) {
        Optional<T> extractContent = extractor.extract(request);
        if (!extractContent.isPresent()) {
            return null;
        }

        T target = extractContent.get();
        if (target instanceof String) {
            return (String)target;
        }

        if (target instanceof String[]) {
            String[] contents = (String[])target;
            return contents[0];
        }

        return target.toString();
    }
}
