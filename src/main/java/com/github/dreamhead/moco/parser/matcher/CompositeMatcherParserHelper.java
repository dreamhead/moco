package com.github.dreamhead.moco.parser.matcher;

import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.parser.model.RequestSetting;

import java.util.Collection;

import static com.github.dreamhead.moco.Moco.and;

public class CompositeMatcherParserHelper {

    public static RequestMatcher createRequestMatcher(RequestSetting request, Collection<RequestMatcher> matchers) {
        switch (matchers.size()) {
            case 0:
                throw new IllegalArgumentException("illegal request setting:" + request);
            case 1:
                return matchers.iterator().next();
            default:
                return and(matchers.toArray(new RequestMatcher[matchers.size()]));
        }
    }
}
