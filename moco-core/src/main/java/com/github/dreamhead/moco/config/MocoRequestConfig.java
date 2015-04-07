package com.github.dreamhead.moco.config;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.RequestMatcher;

import static com.github.dreamhead.moco.Moco.and;

public class MocoRequestConfig implements MocoConfig<RequestMatcher> {
    private RequestMatcher requestMatcher;

    public MocoRequestConfig(RequestMatcher requestMatcher) {
        this.requestMatcher = requestMatcher;
    }

    @Override
    public boolean isFor(String id) {
        return REQUEST_ID.equalsIgnoreCase(id);
    }

    @Override
    public RequestMatcher apply(RequestMatcher target) {
        return and(requestMatcher, target);
    }
}
