package com.github.dreamhead.moco.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dreamhead.moco.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.uri;

public class HttpServerParser {
    public HttpServer parseServer(InputStream is) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonSetting jsonSetting = mapper.readValue(is, JsonSetting.class);
        return createHttpServer(jsonSetting);
    }

    private HttpServer createHttpServer(JsonSetting jsonSetting) {
        HttpServer server = new HttpServer(jsonSetting.getPort());
        List<SessionSetting> sessions = jsonSetting.getSessions();
        for (SessionSetting session : sessions) {
            if (session.isAnyResponse()) {
                server.response(session.getResponse().getText());
            } else {
                server.request(by(uri(session.getRequest().getUri()))).response(session.getResponse().getText());
            }
        }

        return server;
    }
}
