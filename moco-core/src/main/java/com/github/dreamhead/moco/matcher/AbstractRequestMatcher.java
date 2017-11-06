package com.github.dreamhead.moco.matcher;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.RequestMatcher;

public abstract class AbstractRequestMatcher implements RequestMatcher {
    public abstract RequestMatcher doApply(MocoConfig config);

    @Override
    @SuppressWarnings("unchecked")
    public final RequestMatcher apply(final MocoConfig config) {
        if (config.isFor(MocoConfig.REQUEST_ID)) {
            return (RequestMatcher) config.apply(this);
        }

        return doApply(config);
    }
}
