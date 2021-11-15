package com.github.dreamhead.moco.matcher;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.extractor.ContentRequestExtractor;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.collect.Streams;

public final class JsonStructRequestMatcher extends JsonRequestMatcher {
    public JsonStructRequestMatcher(final Resource expected, final ContentRequestExtractor extractor) {
        super(expected, extractor);
    }

    protected boolean doMatch(final JsonNode requestNode, final JsonNode resourceNode) {
        if (requestNode == null) {
            return false;
        }

        if (resourceNode.isNull()) {
            return true;
        }

        if (requestNode.isNumber() && resourceNode.isNumber()) {
            return true;
        }

        if (requestNode.isBoolean() && resourceNode.isBoolean()) {
            return true;
        }

        if (requestNode.isTextual() && resourceNode.isTextual()) {
            return true;
        }

        if (requestNode.isObject() && resourceNode.isObject()) {
            return Streams.stream(resourceNode.fieldNames())
                    .allMatch(name -> doMatch(requestNode.get(name), resourceNode.get(name)));
        }

        if (requestNode.isArray() && resourceNode.isArray()) {
            if (requestNode.isEmpty()) {
                return true;
            }
            JsonNode templateNode = requestNode.get(0);
            return Streams.stream(resourceNode)
                    .allMatch(node -> doMatch(templateNode, node));
        }

        return false;
    }

    @Override
    protected RequestMatcher newApplyMatcher(final Resource appliedResource, final ContentRequestExtractor extractor) {
        return new JsonStructRequestMatcher(appliedResource, extractor);
    }
}
