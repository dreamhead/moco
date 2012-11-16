package com.github.dreamhead.moco.matcher;

import com.github.dreamhead.moco.RequestExtractor;
import com.github.dreamhead.moco.RequestMatcher;
import org.jboss.netty.handler.codec.http.HttpRequest;

public class EqRequestMatcher implements RequestMatcher {
    private RequestExtractor extractor;
    private String expected;

    public EqRequestMatcher(RequestExtractor extractor, String expected) {
        this.extractor = extractor;
        this.expected = expected;
    }

    @Override
    public boolean match(HttpRequest request) {
        return this.expected.equals(extractor.extract(request));
    }
}
