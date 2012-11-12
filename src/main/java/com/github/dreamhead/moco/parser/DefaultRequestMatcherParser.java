package com.github.dreamhead.moco.parser;

import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.parser.matcher.*;
import com.github.dreamhead.moco.parser.model.RequestSetting;
import com.google.common.base.Function;
import com.google.common.base.Predicate;

import java.util.Collection;
import java.util.List;

import static com.github.dreamhead.moco.Moco.and;
import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.Lists.newArrayList;

public class DefaultRequestMatcherParser implements RequestMatcherParser {
    static List<MatcherParser> parsers = newArrayList(
            new UriMatcherParser(),
            new TextMatcherParser(),
            new FileMatcherParser(),
            new MethodMatcherParser(),
            new HeadersMatcherParser(),
            new XpathMatcherParser(),
            new QueriesMatcherParser()
    );

    public static RequestMatcher wrapRequestMatcher(RequestSetting request, Collection<RequestMatcher> matchers) {
        switch (matchers.size()) {
            case 0:
                throw new IllegalArgumentException("illegal request setting:" + request);
            case 1:
                return matchers.iterator().next();
            default:
                return and(matchers.toArray(new RequestMatcher[matchers.size()]));
        }
    }

    private Function<MatcherParser, RequestMatcher> parseRequestMatcher(final RequestSetting request) {
        return new Function<MatcherParser, RequestMatcher>() {
            @Override
            public RequestMatcher apply(MatcherParser parser) {
                return parser.parse(request);
            }
        };
    }

    private Predicate<RequestMatcher> filterEmptyMatcher() {
        return new Predicate<RequestMatcher>() {
            @Override
            public boolean apply(RequestMatcher matcher) {
                return matcher != null;
            }
        };
    }

    private Collection<RequestMatcher> parseRequestMatchers(final RequestSetting request) {
        return filter(transform(parsers, parseRequestMatcher(request)), filterEmptyMatcher());
    }

    @Override
    public RequestMatcher createRequestMatcher(RequestSetting request) {
        return wrapRequestMatcher(request, parseRequestMatchers(request));
    }
}
