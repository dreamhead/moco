package com.github.dreamhead.moco.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dreamhead.moco.HeaderResponseHandler;
import com.github.dreamhead.moco.HttpServer;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.handler.AndResponseHandler;
import com.github.dreamhead.moco.handler.ContentHandler;
import com.github.dreamhead.moco.handler.StatusCodeResponseHandler;
import com.github.dreamhead.moco.parser.model.JsonSetting;
import com.github.dreamhead.moco.parser.model.ResponseSetting;
import com.github.dreamhead.moco.parser.model.SessionSetting;
import com.google.common.base.Function;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Collections2.transform;
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

    private ResponseHandler getContent(SessionSetting session) throws IOException {
        ResponseSetting response = session.getResponse();
        if (response.getText() != null) {
            return new ContentHandler(response.getText());
        } else if (response.getFile() != null) {
            return new ContentHandler(toByteArray(new FileInputStream(response.getFile())));
        } else if (response.getStatus() != null) {
            return new StatusCodeResponseHandler(Integer.parseInt(response.getStatus()));
        } else if (response.getHeaders() != null) {
            Map<String,String> headers = response.getHeaders();
            Collection<ResponseHandler> collection = transform(headers.entrySet(), toHeaderResponseHandler());
            return new AndResponseHandler(collection.toArray(new ResponseHandler[collection.size()]));
        } else if (response.getUrl() != null) {
            URL url = new URL(response.getUrl());
            return new ContentHandler(toByteArray(url.openStream()));
        }

        throw new IllegalArgumentException("unknown response setting with " + session);
    }

    private Function<Map.Entry<String, String>, ResponseHandler> toHeaderResponseHandler() {
        return new Function<Map.Entry<String, String>, ResponseHandler>() {
            @Override
            public ResponseHandler apply(Map.Entry<String, String> entry) {
                return new HeaderResponseHandler(entry.getKey(), entry.getValue());
            }
        };
    }
}
