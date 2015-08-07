package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.ResponseSetting;
import com.github.dreamhead.moco.Server;

import static com.github.dreamhead.moco.Moco.or;
import static com.google.common.base.Preconditions.checkNotNull;

public abstract class BaseServer<T extends ResponseSetting<T>>
        extends BaseResponseSettingConfiguration<T> implements Server<T> {
    protected abstract T onRequestAttached(final RequestMatcher matcher);

    public T request(final RequestMatcher matcher) {
        return this.onRequestAttached(checkNotNull(matcher, "Matcher should not be null"));
    }

    public T request(final RequestMatcher... matchers) {
        return request(or(checkNotNull(matchers, "Matcher should not be null")));
    }
}
