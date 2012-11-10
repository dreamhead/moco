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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.github.dreamhead.moco.Moco.and;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.io.ByteStreams.toByteArray;

public class HttpServerParser {
    private List<MatcherParser> parsers = newArrayList(new UriMatcherParser(), new TextMatcherParser(), new FileMatcherParser(), new MethodMatcherParser());

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
                server.request(createMatcher(session)).response(getContent(session));
            }
        }

        return server;
    }

    private RequestMatcher createMatcher(SessionSetting session) {
        return createRequestMatcher(session.getRequest(), parseRequestMatchers(session.getRequest()));
    }

    private List<RequestMatcher> parseRequestMatchers(RequestSetting request) {
        List<RequestMatcher> matchers = newArrayList();
        for (MatcherParser matcherParser : parsers) {
            RequestMatcher matcher = matcherParser.parse(request);
            if (matcher != null) {
                matchers.add(matcher);
            }
        }
        return matchers;
    }

    private RequestMatcher createRequestMatcher(RequestSetting request, List<RequestMatcher> matchers) {
        switch (matchers.size()) {
            case 0:
                throw new IllegalArgumentException("unknown request setting with " + request);
            case 1:
                return matchers.get(0);
            default:
                return and(matchers.toArray(new RequestMatcher[matchers.size()]));
        }
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
