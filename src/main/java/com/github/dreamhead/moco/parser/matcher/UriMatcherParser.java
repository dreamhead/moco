package com.github.dreamhead.moco.parser.matcher;

import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.parser.model.RequestSetting;

import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.uri;

public class UriMatcherParser implements MatcherParser {
    @Override
    public RequestMatcher parse(RequestSetting request) {
        String uri = request.getUri();
        if (uri == null) {
            return null;
        }

        return by(uri(uri));

    }
}
