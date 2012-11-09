package com.github.dreamhead.moco.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dreamhead.moco.HttpServer;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.handler.ContentHandler;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.text;
import static com.github.dreamhead.moco.Moco.uri;
import static com.google.common.io.ByteStreams.toByteArray;

public class HttpServerParser {
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
        RequestSetting request = session.getRequest();
        String uri = request.getUri();
        if (uri != null) {
            return by(uri(uri));
        }
        String requestText = request.getText();
        if (requestText != null) {
            return by(text(requestText));
        }

        throw new IllegalArgumentException("unknown request setting with " + session);
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
