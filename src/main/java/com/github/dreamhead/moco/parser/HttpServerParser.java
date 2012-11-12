package com.github.dreamhead.moco.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dreamhead.moco.HttpServer;
import com.github.dreamhead.moco.handler.ContentHandler;
import com.github.dreamhead.moco.parser.model.JsonSetting;
import com.github.dreamhead.moco.parser.model.ResponseSetting;
import com.github.dreamhead.moco.parser.model.SessionSetting;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.google.common.io.ByteStreams.toByteArray;

public class HttpServerParser {
    private RequestMatcherParser requestMatcherParser = new DynamicRequestMatcherParser();

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
                server.request(requestMatcherParser.createRequestMatcher(session.getRequest())).response(getContent(session));
            }
        }

        return server;
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
