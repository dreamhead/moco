package com.github.dreamhead.moco;

public interface Server<T extends ResponseSetting> extends ResponseSetting<T> {
    int port();

    T request(RequestMatcher matcher);

    T request(RequestMatcher... matchers);
}
