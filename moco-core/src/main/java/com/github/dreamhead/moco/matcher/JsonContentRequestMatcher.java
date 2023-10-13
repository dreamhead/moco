package com.github.dreamhead.moco.matcher;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.extractor.ContentRequestExtractor;
import com.github.dreamhead.moco.resource.Resource;

public final class JsonContentRequestMatcher extends JsonRequestMatcher {
    public JsonContentRequestMatcher(final Resource expected, final ContentRequestExtractor extractor) {
        super(expected, extractor);
    }

    @Override
    protected boolean doMatch(final JsonNode actual, final JsonNode expected) {
        return actual.equals(expected);
    }

    @Override
    protected RequestMatcher newApplyMatcher(final Resource appliedResource, final ContentRequestExtractor extractor) {
        return new JsonContentRequestMatcher(appliedResource, extractor);
    }
}
