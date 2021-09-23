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
import com.google.common.collect.Streams;
import org.springframework.expression.*;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.*;


public final class JsonRequestMatcher extends AbstractRequestMatcher {
    private final ContentRequestExtractor extractor;
    private final ObjectMapper mapper;
    private final Resource expected;
    private final JsonMatchMode matchMode;
    private final ExpressionParser parser;

    public JsonRequestMatcher(final Resource expected, final ContentRequestExtractor extractor) {
        this(expected, extractor, JsonMatchMode.CONTENT);
    }

    public JsonRequestMatcher(final Resource expected, final ContentRequestExtractor extractor, final JsonMatchMode matchMode) {
        this.extractor = extractor;
        this.expected = expected;
        this.matchMode = matchMode;
        this.mapper = new ObjectMapper();
        this.parser = new SpelExpressionParser();
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
                case RULE:
                    return doRuleMatch(requestNode, resourceNode, request);
                default:
                    return doContentMatch(requestNode, resourceNode);
            }
        } catch (JsonProcessingException | ExpressionException e) {
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
            JsonNode templateNode = resourceNode.get(0);
            return Streams.stream(requestNode)
                    .allMatch(node -> doStructMatch(templateNode, node));
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

    private boolean doRuleMatch(final JsonNode requestNode, final JsonNode resourceNode, Request request) {

        if (requestNode == null || resourceNode.isNull()) {
            return true;
        }

        // If it is a JSON String value,it will be as a expression
        if (resourceNode.isTextual()) {

            EvaluationContext context = new StandardEvaluationContext();

            Expression exp = parser.parseExpression(resourceNode.asText());

            context.setVariable("value", transform(requestNode));

            context.setVariable("request", request);

            return Optional.ofNullable(exp.getValue(context, Boolean.class)).orElse(false);

        }
        // If it is a JSON boolean value or numeric JSON value,it  will only be as a type to compare
        if (resourceNode.isNumber()) {
            return requestNode.isNumber();
        }

        if (resourceNode.isBoolean()) {
            return requestNode.isBoolean();
        }

        if (resourceNode.isObject() && requestNode.isObject()) {

            return Streams.stream(resourceNode.fieldNames())
                    .allMatch(name -> doRuleMatch(requestNode.get(name), resourceNode.get(name), request));
        }

        if (resourceNode.isArray() && requestNode.isArray()) {
            if (resourceNode.isEmpty()) {
                return true;
            }
            JsonNode templateNode = resourceNode.get(0);
            return Streams.stream(requestNode)
                    .allMatch(node -> doRuleMatch(templateNode, node, request));
        }


        return false;
    }

    private Object transform(JsonNode jsonNode) {

        if (jsonNode == null || jsonNode.isNull()) {
            return null;
        }

        if (jsonNode.isBoolean()) {
            return jsonNode.asBoolean();
        }

        if (jsonNode.isNumber()) {
            return jsonNode.asDouble();
        }

        if (jsonNode.isTextual()) {
            return jsonNode.asText();
        }

        if (jsonNode.isArray()) {
            List<Object> list = new ArrayList<>();
            Streams.stream(jsonNode)
                    .forEach(node -> list.add(transform(node)));
            return list;
        }
        if (jsonNode.isObject()) {
            Map<String, Object> map = new HashMap<>();
            Streams.stream(jsonNode.fieldNames())
                    .forEach(name -> map.put(name, transform(jsonNode.get(name))));
            return map;
        }
        return null;
    }


    public enum JsonMatchMode {
        //matched pattern
        CONTENT,
        STRUCT,
        RULE
    }
}
