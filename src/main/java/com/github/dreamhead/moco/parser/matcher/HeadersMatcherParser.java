package com.github.dreamhead.moco.parser.matcher;

import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.parser.model.RequestSetting;

import java.util.List;
import java.util.Map;

import static com.github.dreamhead.moco.Moco.and;
import static com.github.dreamhead.moco.Moco.eq;
import static com.github.dreamhead.moco.Moco.header;
import static com.google.common.collect.Lists.newArrayList;

public class HeadersMatcherParser implements MatcherParser {
    @Override
    public RequestMatcher parse(RequestSetting request) {
        Map<String,String> headers = request.getHeaders();
        if (headers == null) {
            return null;
        }

        List<RequestMatcher> matchers = newArrayList();

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            matchers.add(eq(header(entry.getKey()), entry.getValue()));
        }

        return and(matchers.toArray(new RequestMatcher[matchers.size()]));
    }
}
