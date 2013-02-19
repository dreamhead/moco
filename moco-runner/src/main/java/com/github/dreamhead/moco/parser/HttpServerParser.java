package com.github.dreamhead.moco.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.dreamhead.moco.HeaderResponseHandler;
import com.github.dreamhead.moco.HttpServer;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.handler.AndResponseHandler;
import com.github.dreamhead.moco.handler.ContentHandler;
import com.github.dreamhead.moco.parser.model.MountSetting;
import com.github.dreamhead.moco.parser.model.ResponseSetting;
import com.github.dreamhead.moco.parser.model.SessionSetting;
import com.google.common.base.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.github.dreamhead.moco.Moco.status;
import static com.github.dreamhead.moco.MocoCache.cache;
import static com.github.dreamhead.moco.MocoMount.to;
import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;

public class HttpServerParser {
    private static Logger logger = LoggerFactory.getLogger(HttpServer.class);

    private RequestMatcherParser requestMatcherParser = new DynamicRequestMatcherParser();
    private final ObjectMapper mapper = new ObjectMapper();
    private final TypeFactory factory = TypeFactory.defaultInstance();

    public HttpServer parseServer(InputStream is, int port) throws IOException {
        List<SessionSetting> sessionSettings = readSessions(is);
        return createHttpServer(sessionSettings, port);
    }

    private List<SessionSetting> readSessions(InputStream is) {
        try {
            return mapper.readValue(is, factory.constructCollectionType(List.class, SessionSetting.class));
        } catch (UnrecognizedPropertyException e) {
            logger.info("Unrecognized field", e);
            throw new RuntimeException(format("Unrecognized field [ %s ], please check!", e.getUnrecognizedPropertyName()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpServer createHttpServer(List<SessionSetting> sessionSettings, int port) throws IOException {
        HttpServer server = new HttpServer(port);
        for (SessionSetting session : sessionSettings) {
            logger.debug("Parse session: {}", session);

            if (session.isMount()) {
                MountSetting mount = session.getMount();
                server.mount(mount.getDir(), to(mount.getUri()), mount.getMountPredicates());
            } else if (session.isAnyResponse()) {
                server.response(getContent(session));
            } else {
                server.request(requestMatcherParser.createRequestMatcher(session.getRequest())).response(getContent(session));
            }
        }

        return server;
    }

    private ResponseHandler getContent(SessionSetting session) throws IOException {
        return getResponseHandler(session.getResponse());
    }

    private ResponseHandler getResponseHandler(ResponseSetting response) {
        List<ResponseHandler> handlers = newArrayList();
        if (response.isResource()) {
            handlers.add(new ContentHandler(response.retrieveResource()));
        }

        if (response.getStatus() != null) {
            handlers.add(status(Integer.parseInt(response.getStatus())));
        }

        if (response.getHeaders() != null) {
            Map<String, String> headers = response.getHeaders();
            Collection<ResponseHandler> collection = transform(headers.entrySet(), toHeaderResponseHandler());
            handlers.add(compositeResponseHandlers(collection));
        }

        if (handlers.isEmpty()) {
            throw new IllegalArgumentException("unknown response setting with " + response);
        }

        return handlers.size() == 1 ? handlers.get(0) : compositeResponseHandlers(handlers);
    }

    private ResponseHandler compositeResponseHandlers(Collection<ResponseHandler> collection) {
        ResponseHandler[] headerHandlers = collection.toArray(new ResponseHandler[collection.size()]);
        return new AndResponseHandler(headerHandlers);
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
