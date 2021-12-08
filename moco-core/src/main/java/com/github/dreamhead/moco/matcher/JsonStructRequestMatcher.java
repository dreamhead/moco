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

    protected boolean doMatch(final JsonNode actual, final JsonNode expected) {
        if (actual == null) {
            return false;
        }

        if (expected.isNull()) {
            return true;
        }

        if (actual.isNumber() && expected.isNumber()) {
            return true;
        }

        if (actual.isBoolean() && expected.isBoolean()) {
            return true;
        }

        if (actual.isTextual() && expected.isTextual()) {
            return true;
        }

        if (actual.isObject() && expected.isObject()) {
            return Streams.stream(expected.fieldNames())
                    .allMatch(name -> doMatch(actual.get(name), expected.get(name)));
        }

        if (actual.isArray() && expected.isArray()) {
            if (actual.isEmpty()) {
                return true;
            }
            JsonNode templateNode = actual.get(0);
            return Streams.stream(expected)
                    .allMatch(node -> doMatch(templateNode, node));
        }

        if (actual.isBinary() && expected.isBinary()) {
            return true;
        }

        return false;
    }

    @Override
    protected RequestMatcher newApplyMatcher(final Resource appliedResource, final ContentRequestExtractor extractor) {
        return new JsonStructRequestMatcher(appliedResource, extractor);
    }
}
