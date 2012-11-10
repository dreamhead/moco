package com.github.dreamhead.moco.parser.matcher;

import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.parser.model.RequestSetting;

import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.text;

public class TextMatcherParser implements MatcherParser {

    @Override
    public RequestMatcher parse(RequestSetting request) {
        String requestText = request.getText();
        if (requestText == null) {
            return null;
        }

        return by(text(requestText));
    }
}
