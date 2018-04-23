package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.ResponseSetting;
import com.github.dreamhead.moco.Server;

import static com.github.dreamhead.moco.Moco.or;
import static com.github.dreamhead.moco.util.Iterables.head;
import static com.github.dreamhead.moco.util.Iterables.tail;
import static com.google.common.base.Preconditions.checkNotNull;

public abstract class BaseServer<T extends ResponseSetting<T>>
        extends BaseResponseSettingConfiguration<T> implements Server<T> {
    protected abstract T onRequestAttached(RequestMatcher matcher);

    public final T request(final RequestMatcher matcher) {
        return this.onRequestAttached(checkNotNull(matcher, "Matcher should not be null"));
    }

    public final T request(final RequestMatcher... matchers) {
        checkNotNull(matchers, "Matcher should not be null");
        return request(or(head(matchers), tail(matchers)));
    }
}
