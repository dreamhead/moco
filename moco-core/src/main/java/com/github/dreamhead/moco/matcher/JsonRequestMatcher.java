package com.github.dreamhead.moco.matcher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.MocoException;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.extractor.ContentRequestExtractor;
import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.base.Optional;

import java.io.IOException;

public final class JsonRequestMatcher extends AbstractRequestMatcher {
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
        return content.isPresent() && doMatch(request, content.get());
    }

    private boolean doMatch(final Request request, final MessageContent content) {
        try {
            JsonNode requestNode = mapper.readTree(content.toString());
            JsonNode resourceNode = mapper.readTree(expected.readFor(request).toString());
            return requestNode.equals(resourceNode);
        } catch (JsonProcessingException jpe) {
            return false;
        } catch (IOException e) {
            throw new MocoException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public RequestMatcher doApply(final MocoConfig config) {
        Resource appliedResource = this.expected.apply(config);
        if (appliedResource == this.expected) {
            return this;
        }

        return new JsonRequestMatcher(appliedResource, this.extractor);
    }
}
