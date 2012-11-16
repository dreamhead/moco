package com.github.dreamhead.moco.parser;

import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.parser.model.RequestSetting;

public interface RequestMatcherParser {
    RequestMatcher createRequestMatcher(RequestSetting request);
}
