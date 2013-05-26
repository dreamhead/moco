package com.github.dreamhead.moco.matcher;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.RequestExtractor;
import com.github.dreamhead.moco.RequestMatcher;
import org.jboss.netty.handler.codec.http.HttpRequest;

import java.util.regex.Pattern;

public class MatchMatcher implements RequestMatcher {
    private final RequestExtractor extractor;
    private final Pattern pattern;

    public MatchMatcher(RequestExtractor extractor, Pattern pattern) {
        this.extractor = extractor;
        this.pattern = pattern;
    }

    @Override
    public boolean match(HttpRequest request) {
        String target = extractor.extract(request);
        return target != null && pattern.matcher(target).matches();
    }

    @Override
    public void apply(MocoConfig config) {
    }
}
