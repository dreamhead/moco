package com.github.dreamhead.moco.matcher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.MocoException;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.RequestExtractor;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.base.Optional;

import java.io.IOException;

import static com.google.common.base.Optional.of;

public class JsonRequestMatcher extends AbstractRequestMatcher {
    private final RequestExtractor<byte[]> extractor;
    private final Resource expected;
    private final ObjectMapper mapper;

    public JsonRequestMatcher(final RequestExtractor<byte[]> extractor, final Resource expected) {
        this.extractor = extractor;
        this.expected = expected;
        this.mapper = new ObjectMapper();
    }

    @Override
    public boolean match(final Request request) {
        Optional<byte[]> content = extractor.extract(request);
        return content.isPresent() && doMatch(request, content.get());
    }

    private boolean doMatch(final Request request, final byte[] content) {
        try {
            JsonNode requestNode = mapper.readTree(content);
            JsonNode resourceNode = mapper.readTree(expected.readFor(of(request)).getContent());
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

        return new JsonRequestMatcher(this.extractor, appliedResource);
    }
}
