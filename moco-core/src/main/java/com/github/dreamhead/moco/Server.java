package com.github.dreamhead.moco;

public interface Server<T extends ResponseSetting> extends ResponseSetting<T> {
    int port();

    T request(final RequestMatcher matcher);

    T request(final RequestMatcher... matchers);
}
