package com.github.dreamhead.moco.parser.matcher;

import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.parser.model.RequestSetting;
import com.google.common.base.Function;

import java.util.Map;

import static com.github.dreamhead.moco.Moco.eq;
import static com.github.dreamhead.moco.Moco.query;

public class QueriesMatcherParser extends AbstractCompositeMatcherParser {
    @Override
    protected Map<String, String> getCollection(RequestSetting request) {
        return request.getQueries();
    }

    @Override
    protected Function<Map.Entry<String, String>, RequestMatcher> toTargetMatcher() {
        return new Function<Map.Entry<String, String>, RequestMatcher>() {
            @Override
            public RequestMatcher apply(Map.Entry<String, String> input) {
                return eq(query(input.getKey()), input.getValue());
            }
        };
    }
}
