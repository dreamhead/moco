package com.github.dreamhead.moco.matcher;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.RequestExtractor;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.resource.Resource;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionException;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public final class RuleRequestMatcher<T> extends AbstractRequestMatcher {

    private final RequestExtractor<T> extractor;

    private final Resource expected;

    private final ExpressionParser parser;

    public RuleRequestMatcher(final Resource expected, final RequestExtractor<T> extractor) {

        this.extractor = extractor;

        this.expected = expected;

        this.parser = new SpelExpressionParser();
    }


    @Override
    public RequestMatcher doApply(final MocoConfig config) {
        Resource appliedResource = this.expected.apply(config);
        if (appliedResource == this.expected) {
            return this;
        }

        return new RuleRequestMatcher<>(appliedResource, this.extractor);
    }

    @Override
    public boolean match(Request request) {
        Optional<T> extractContent = extractor.extract(request);
        String rule = expected.readFor(request).toString();
        return extractContent.filter(content -> this.matchContent(content
                , rule, request)).isPresent();
    }


    private boolean matchContent(final T target, final String rule, final Request request) {
        if (target instanceof String) {
            return ruleMatch((String) target, rule, request);
        }

        if (target instanceof String[]) {
            String[] contents = (String[]) target;
            return Arrays.stream(contents).filter(Objects::nonNull).anyMatch(content -> ruleMatch(content, rule, request));
        }

        if (target instanceof MessageContent) {
            MessageContent actualTarget = (MessageContent) target;
            return ruleMatch(actualTarget.toString(), rule, request);
        }

        return false;
    }

    private boolean ruleMatch(final String content, final String rule, final Request request) {

        try {
            EvaluationContext context = new StandardEvaluationContext();
            Expression exp = parser.parseExpression(rule);
            context.setVariable("value", content);
            context.setVariable("request", request);
            return Optional.ofNullable(exp.getValue(context, Boolean.class)).orElse(false);
        } catch (ExpressionException e) {
            return false;
        }
    }
}
