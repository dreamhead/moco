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
import com.google.common.collect.Iterables;
import com.google.common.collect.Streams;

import java.util.Iterator;
import java.util.Optional;


public final class JsonRequestMatcher extends AbstractRequestMatcher {
    private final ContentRequestExtractor extractor;
    private final ObjectMapper mapper;
    private final Resource expected;
    private final JsonMatchMode matchMode;

    public JsonRequestMatcher(final Resource expected, final ContentRequestExtractor extractor) {
        this(expected, extractor, JsonMatchMode.CONTENT);
    }

    public JsonRequestMatcher(final Resource expected, final ContentRequestExtractor extractor, JsonMatchMode matchMode) {
        this.extractor = extractor;
        this.expected = expected;
        this.matchMode = matchMode;
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
            JsonNode requestNode = mapper.readTree(content.toString());
            JsonNode resourceNode = mapper.readTree(expected.readFor(request).toString());
            switch (this.matchMode) {
                case STRUCT:
                    return doStructMatch(requestNode, resourceNode);
//                case REGEX :
//                   return doRegexMatch(requestNode,resourceNode);
                default:
                    return doContentMatch(requestNode, resourceNode);
            }
        } catch (JsonProcessingException jpe) {
            return false;
        }
    }

    private boolean doContentMatch(final JsonNode requestNode, final JsonNode resourceNode) {
        return requestNode.equals(resourceNode);
    }

    private boolean doStructMatch(final JsonNode requestNode, final JsonNode resourceNode) {
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
                    .allMatch(name -> doStructMatch(requestNode.get(name), resourceNode.get(name)));
        }

        if (requestNode.isArray() && resourceNode.isArray()) {
            if (requestNode.isEmpty()) {
                return true;
            }
            JsonNode templateNode = requestNode.get(0);
            for (JsonNode elementNode : resourceNode) {
                if (!(doStructMatch(templateNode, elementNode))) {
                    return false;
                }
            }

            return true;
        }
        return false;
    }

    @Override
    public RequestMatcher doApply(final MocoConfig config) {
        Resource appliedResource = this.expected.apply(config);
        if (appliedResource == this.expected) {
            return this;
        }

        return new JsonRequestMatcher(appliedResource, this.extractor, this.matchMode);
    }


    public enum JsonMatchMode {
        //matched pattern
        CONTENT,
        STRUCT,
        REGEX
    }
}
