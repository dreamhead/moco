package com.github.dreamhead.moco.parser.matcher;

import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.matcher.GetMethodRequestMatcher;
import com.github.dreamhead.moco.matcher.PostMethodRequestMatcher;
import com.github.dreamhead.moco.parser.model.RequestSetting;

public class MethodMatcherParser implements MatcherParser {
    @Override
    public RequestMatcher parse(RequestSetting request) {
        String method = request.getMethod();
        if (method == null) {
            return null;
        }

        if (method.equalsIgnoreCase("get")) {
            return new GetMethodRequestMatcher();
        }

        if (method.equalsIgnoreCase("post")) {
            return new PostMethodRequestMatcher();
        }

        throw new UnsupportedOperationException("unsupported request method:" + method);
    }
}
