package com.github.dreamhead.moco.matcher;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.RequestExtractor;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.resource.Resource;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

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

    protected Resource getExpected() {
        return expected;
    }

    @Override
    public final boolean match(final Request request) {
        Optional<T> extractContent = extractor.extract(request);
        return extractContent.filter(this::matchContent).isPresent();
    }

    private boolean matchContent(final T target) {
        if (target instanceof String) {
            return predicate.test((String) target);
        }

        if (target instanceof String[]) {
            String[] contents = (String[]) target;
            return Arrays.stream(contents).filter(Objects::nonNull).anyMatch(predicate);
        }

        if (target instanceof MessageContent) {
            MessageContent actualTarget = (MessageContent) target;
            return predicate.test(actualTarget.toString());
        }

        return false;
    }

    @Override
    public final RequestMatcher doApply(final MocoConfig config) {
        Resource appliedResource = expected.apply(config);
        if (appliedResource == expected) {
            return this;
        }

        return newMatcher(extractor, appliedResource);
    }
}
