package com.github.dreamhead.moco.matcher;

import com.github.dreamhead.moco.RequestExtractor;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.internal.ContextKey;
import com.github.dreamhead.moco.internal.SessionContext;
import com.github.dreamhead.moco.resource.Resource;
import com.github.dreamhead.moco.util.AntPathMatcher;

public class PathMatcher<T> extends AbstractOperatorMatcher<T> {
    private static final AntPathMatcher matcher = new AntPathMatcher();

    public PathMatcher(final RequestExtractor<T> extractor, final Resource expected) {
        super(extractor, expected, input -> {
            final String target = expected.readFor(null).toString();
            return matcher.match(target, input);
        });
    }

    @Override
    public void attach(final SessionContext context) {
        context.register(ContextKey.PATH, getExpected());
    }

    @Override
    protected RequestMatcher newMatcher(final RequestExtractor<T> extractor, final Resource resource) {
        return new PathMatcher<>(extractor, resource);
    }
}
