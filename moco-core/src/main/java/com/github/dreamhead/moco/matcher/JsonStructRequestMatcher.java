package com.github.dreamhead.moco.matcher;

import tools.jackson.databind.JsonNode;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.extractor.ContentRequestExtractor;
import com.github.dreamhead.moco.resource.Resource;

import java.util.stream.StreamSupport;

public final class JsonStructRequestMatcher extends JsonRequestMatcher {
    public JsonStructRequestMatcher(final Resource expected, final ContentRequestExtractor extractor) {
        super(expected, extractor);
    }

    @Override
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

        if (actual.isString() && expected.isString()) {
            return true;
        }

        if (actual.isObject() && expected.isObject()) {
            return expected.properties().stream()
                    .map(p -> p.getKey())
                    .allMatch(name -> doMatch(actual.get(name), expected.get(name)));
        }

        if (actual.isArray() && expected.isArray()) {
            if (actual.isEmpty() || expected.isEmpty()) {
                return true;
            }
            JsonNode templateNode = expected.get(0);
            return StreamSupport.stream(actual.spliterator(), false)
                    .allMatch(node -> doMatch(node, templateNode));
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
