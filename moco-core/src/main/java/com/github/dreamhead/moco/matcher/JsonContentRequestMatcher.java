package com.github.dreamhead.moco.matcher;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.extractor.ContentRequestExtractor;
import com.github.dreamhead.moco.resource.Resource;

public class JsonContentRequestMatcher extends JsonRequestMatcher {
    public JsonContentRequestMatcher(final Resource expected, final ContentRequestExtractor extractor) {
        super(expected, extractor);
    }

    protected boolean doMatch(final JsonNode requestNode, final JsonNode resourceNode) {
        return requestNode.equals(resourceNode);
    }

    @Override
    protected RequestMatcher newApplyMatcher(final Resource appliedResource, final ContentRequestExtractor extractor) {
        return new JsonContentRequestMatcher(appliedResource, extractor);
    }
}
