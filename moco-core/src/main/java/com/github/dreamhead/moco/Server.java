package com.github.dreamhead.moco;

import com.github.dreamhead.moco.resource.Resource;

public interface Server<T extends ResponseSetting> extends ResponseSetting<T> {
    int port();

    T request(RequestMatcher matcher);

    T request(Resource matcher);

    T request(RequestMatcher... matchers);
}
