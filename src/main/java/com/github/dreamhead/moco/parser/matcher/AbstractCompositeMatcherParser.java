package com.github.dreamhead.moco.parser.matcher;

import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.parser.model.RequestSetting;
import com.google.common.base.Function;

import java.util.Map;

import static com.github.dreamhead.moco.parser.DefaultRequestMatcherParser.wrapRequestMatcher;
import static com.google.common.collect.Collections2.transform;

public abstract class AbstractCompositeMatcherParser implements MatcherParser {
    protected abstract Map<String, String> getCollection(RequestSetting request);

    protected abstract Function<Map.Entry<String, String>, RequestMatcher> toTargetMatcher();

    @Override
    public RequestMatcher parse(RequestSetting request) {
        return toRequestMatcher(request, toTargetMatcher());
    }

    private RequestMatcher toRequestMatcher(RequestSetting request, Function<Map.Entry<String, String>, RequestMatcher> function) {
        Map<String, String> collection = getCollection(request);
        if (collection == null) {
            return null;
        }

        return wrapRequestMatcher(request, transform(collection.entrySet(), function));
    }
}
