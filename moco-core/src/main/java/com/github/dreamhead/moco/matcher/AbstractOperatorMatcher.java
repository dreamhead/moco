package com.github.dreamhead.moco.matcher;

import com.github.dreamhead.moco.RequestExtractor;
import com.github.dreamhead.moco.RequestMatcher;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import io.netty.handler.codec.http.FullHttpRequest;

public abstract class AbstractOperatorMatcher<T> implements RequestMatcher {
    protected final RequestExtractor<T> extractor;
    private final Predicate<String> predicate;

    protected AbstractOperatorMatcher(RequestExtractor<T> extractor, Predicate<String> predicate) {
        this.extractor = extractor;
        this.predicate = predicate;
    }

    @Override
    public boolean match(FullHttpRequest request) {
        Optional<T> extractContent = extractor.extract(request);
        if (!extractContent.isPresent()) {
            return false;
        }

        T target = extractContent.get();
        if (target instanceof String) {
            return predicate.apply((String)target);
        }

        if (target instanceof String[]) {
            String[] contents = (String[])target;
            for (String content : contents) {
                if (predicate.apply(content)) {
                    return true;
                }
            }
        }

        return false;
    }
}
