package com.github.dreamhead.moco.matcher;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.RequestExtractor;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;

import static com.google.common.base.Predicates.notNull;
import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.Lists.newArrayList;

public abstract class AbstractOperatorMatcher<T> extends AbstractRequestMatcher {
    protected abstract RequestMatcher newMatcher(RequestExtractor<T> extractor, Resource resource);

    private final RequestExtractor<T> extractor;
    private final Resource expected;
    private final Predicate<String> predicate;

    protected AbstractOperatorMatcher(final RequestExtractor<T> extractor,
                                      final Resource expected,
                                      final Predicate<String> predicate) {
        this.extractor = extractor;
        this.predicate = predicate;
        this.expected = expected;
    }

    @Override
    public final boolean match(final Request request) {
        Optional<T> extractContent = extractor.extract(request);
        if (!extractContent.isPresent()) {
            return false;
        }

        T target = extractContent.get();
        if (target instanceof String) {
            return predicate.apply((String) target);
        }

        if (target instanceof String[]) {
            String[] contents = (String[]) target;
            return from(newArrayList(contents)).filter(notNull()).anyMatch(predicate);
        }

        if (target instanceof MessageContent) {
            MessageContent actualTarget = (MessageContent) target;
            return predicate.apply(actualTarget.toString());
        }

        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final RequestMatcher doApply(final MocoConfig config) {
        Resource appliedResource = expected.apply(config);
        if (appliedResource == expected) {
            return this;
        }

        return newMatcher(extractor, appliedResource);
    }
}
