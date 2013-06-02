package com.github.dreamhead.moco.matcher;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.RequestExtractor;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.resource.Resource;
import org.jboss.netty.handler.codec.http.HttpRequest;

import java.util.Arrays;

public class EqRequestMatcher implements RequestMatcher {
    private final RequestExtractor extractor;
    private final Resource expected;

    public EqRequestMatcher(RequestExtractor extractor, Resource expected) {
        this.extractor = extractor;
        this.expected = expected;
    }

    @Override
    public boolean match(HttpRequest request) {
        String extractContent = extractor.extract(request);
        return extractContent != null && Arrays.equals(extractContent.getBytes(), expected.asByteArray());
    }

    @Override
    public RequestMatcher apply(final MocoConfig config) {
        Resource appliedResource = expected.apply(config);
        if (appliedResource == expected) {
            return this;
        }

        return new EqRequestMatcher(extractor, appliedResource);
    }
}
