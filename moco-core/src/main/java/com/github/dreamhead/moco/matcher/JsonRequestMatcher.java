package com.github.dreamhead.moco.matcher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.extractor.ContentRequestExtractor;
import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.resource.Resource;

import java.util.Optional;

public abstract class JsonRequestMatcher extends AbstractRequestMatcher {
    protected abstract boolean doMatch(final JsonNode actual, final JsonNode expected);
    protected abstract RequestMatcher newApplyMatcher(final Resource appliedResource, final ContentRequestExtractor extractor);

    private final ContentRequestExtractor extractor;
    private final ObjectMapper mapper;
    private final Resource expected;

    public JsonRequestMatcher(final Resource expected, final ContentRequestExtractor extractor) {
        this.extractor = extractor;
        this.expected = expected;
        this.mapper = new ObjectMapper();
    }

    @Override
    public boolean match(final Request request) {
        Optional<MessageContent> content = extractor.extract(request);
        return content.filter(messageContent -> doMatch(request, messageContent))
                .isPresent();
    }

    private boolean doMatch(final Request request, final MessageContent content) {
        try {
            JsonNode actual = mapper.readTree(content.toString());
            JsonNode expected = mapper.readTree(this.expected.readFor(request).toString());
            return doMatch(actual, expected);
        } catch (JsonProcessingException jpe) {
            return false;
        }
    }

    @Override
    public RequestMatcher doApply(final MocoConfig config) {
        Resource appliedResource = this.expected.apply(config);
        if (appliedResource == this.expected) {
            return this;
        }

        return newApplyMatcher(appliedResource, this.extractor);
    }
}
