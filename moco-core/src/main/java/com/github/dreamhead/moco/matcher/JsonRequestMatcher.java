package com.github.dreamhead.moco.matcher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.RequestExtractor;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.resource.Resource;
import org.jboss.netty.handler.codec.http.HttpRequest;

import java.io.IOException;

public class JsonRequestMatcher implements RequestMatcher {
    private final RequestExtractor<String> extractor;
    private final Resource resource;
    private final ObjectMapper mapper;

    public JsonRequestMatcher(RequestExtractor<String> extractor, Resource resource) {
        this.extractor = extractor;
        this.resource = resource;
        this.mapper = new ObjectMapper();
    }

    @Override
    public boolean match(HttpRequest request) {
        try {
            JsonNode requestNode = mapper.readTree(extractor.extract(request));
            JsonNode resourceNode = mapper.readTree(resource.asByteArray());
            return requestNode.equals(resourceNode);
        } catch (JsonProcessingException jpe) {
            return false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public RequestMatcher apply(final MocoConfig config) {
        Resource appliedResource = this.resource.apply(config);
        if (appliedResource == this.resource) {
            return this;
        }

        return new JsonRequestMatcher(this.extractor, appliedResource);
    }
}
