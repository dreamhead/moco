package com.github.dreamhead.moco;

import com.github.dreamhead.moco.matcher.AbstractRequestMatcher;

import static com.github.dreamhead.moco.internal.InternalApis.context;

public interface RequestMatcher extends ConfigApplier<RequestMatcher> {
    boolean match(Request request);

    RequestMatcher ANY_REQUEST_MATCHER = new AbstractRequestMatcher() {
        @Override
        public boolean match(final Request request) {
            return true;
        }

        @Override
        @SuppressWarnings("unchecked")
        public RequestMatcher doApply(final MocoConfig config) {
            if (config.isFor(MocoConfig.URI_ID)) {
                return context((String) config.apply(""));
            }

            return this;
        }
    };
}
