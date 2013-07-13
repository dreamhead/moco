package com.github.dreamhead.moco.parser;

import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.parser.model.RequestSetting;

public interface RequestMatcherFactory {
    RequestMatcher createRequestMatcher(RequestSetting request);
}
