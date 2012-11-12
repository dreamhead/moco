package com.github.dreamhead.moco.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dreamhead.moco.HttpServer;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.handler.ContentHandler;
import com.github.dreamhead.moco.parser.matcher.*;
import com.github.dreamhead.moco.parser.model.JsonSetting;
import com.github.dreamhead.moco.parser.model.RequestSetting;
import com.github.dreamhead.moco.parser.model.ResponseSetting;
import com.github.dreamhead.moco.parser.model.SessionSetting;
import com.google.common.base.Function;
import com.google.common.base.Predicate;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import static com.github.dreamhead.moco.parser.matcher.CompositeMatcherParserHelper.createRequestMatcher;
import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.io.ByteStreams.toByteArray;

public class HttpServerParser {
    private List<MatcherParser> parsers = newArrayList(
            new UriMatcherParser(),
            new TextMatcherParser(),
            new FileMatcherParser(),
            new MethodMatcherParser(),
            new HeadersMatcherParser(),
            new XpathMatcherParser(),
            new QueriesMatcherParser()
    );

    public HttpServer parseServer(InputStream is) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonSetting jsonSetting = mapper.readValue(is, JsonSetting.class);
        return createHttpServer(jsonSetting);
    }

    private HttpServer createHttpServer(JsonSetting jsonSetting) throws IOException {
        HttpServer server = new HttpServer(jsonSetting.getPort());
        List<SessionSetting> sessions = jsonSetting.getSessions();
        for (SessionSetting session : sessions) {
            if (session.isAnyResponse()) {
                server.response(getContent(session));
            } else {
                server.request(createRequestMatcher(session.getRequest(), parseRequestMatchers(session.getRequest()))).response(getContent(session));
            }
        }

        return server;
    }

    private Collection<RequestMatcher> parseRequestMatchers(final RequestSetting request) {
        return filter(transform(parsers, parseRequestMatcher(request)), filterEmptyMatcher());
    }

    private Predicate<RequestMatcher> filterEmptyMatcher() {
        return new Predicate<RequestMatcher>() {
            @Override
            public boolean apply(RequestMatcher matcher) {
                return matcher != null;
            }
        };
    }

    private Function<MatcherParser, RequestMatcher> parseRequestMatcher(final RequestSetting request) {
        return new Function<MatcherParser, RequestMatcher>() {
            @Override
            public RequestMatcher apply(MatcherParser parser) {
                return parser.parse(request);
            }
        };
    }

    private ContentHandler getContent(SessionSetting session) throws IOException {
        ResponseSetting response = session.getResponse();
        if (response.getText() != null) {
            return new ContentHandler(response.getText());
        } else if (response.getFile() != null) {
            String file = response.getFile();
            return new ContentHandler(toByteArray(new FileInputStream(file)));
        }

        throw new IllegalArgumentException("unknown response setting with " + session);
    }
}
