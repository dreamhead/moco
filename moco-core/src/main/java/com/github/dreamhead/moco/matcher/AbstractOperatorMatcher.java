package com.github.dreamhead.moco.matcher;

import com.github.dreamhead.moco.RequestExtractor;
import com.github.dreamhead.moco.RequestMatcher;
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
        T extractContent = extractor.extract(request);
        if (extractContent == null) {
            return false;
        }

        if (extractContent instanceof String) {
            return predicate.apply((String)extractContent);
        }

        if (extractContent instanceof String[]) {
            String[] contents = (String[])extractContent;
            for (String content : contents) {
                if (predicate.apply(content)) {
                    return true;
                }
            }

        }

        return false;
    }
}
