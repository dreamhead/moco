package com.github.dreamhead.moco.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.dreamhead.moco.HeaderResponseHandler;
import com.github.dreamhead.moco.HttpServer;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.handler.AndResponseHandler;
import com.github.dreamhead.moco.handler.ContentHandler;
import com.github.dreamhead.moco.mount.MountPredicate;
import com.github.dreamhead.moco.parser.model.MountSetting;
import com.github.dreamhead.moco.parser.model.ResponseSetting;
import com.github.dreamhead.moco.parser.model.SessionSetting;
import com.google.common.base.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.github.dreamhead.moco.Moco.*;
import static com.github.dreamhead.moco.MocoMount.exclude;
import static com.github.dreamhead.moco.MocoMount.include;
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
                server.mount(mount.getDir(), to(mount.getUri()), getMountPredicates(mount));
            } else if (session.isAnyResponse()) {
                server.response(getContent(session));
            } else {
                server.request(requestMatcherParser.createRequestMatcher(session.getRequest())).response(getContent(session));
            }
        }

        return server;
    }

    private MountPredicate[] getMountPredicates(MountSetting mount) {
        List<MountPredicate> predicates = newArrayList();

        if (mount.getIncludes() != null) {
            for (String include : mount.getIncludes()) {
                predicates.add(include(include));
            }
        }

        if (mount.getExcludes() != null) {
            for (String exclude : mount.getExcludes()) {
                predicates.add(exclude(exclude));
            }
        }

        return predicates.toArray(new MountPredicate[0]);
    }

    private ResponseHandler getContent(SessionSetting session) throws IOException {
        ResponseSetting response = session.getResponse();
        if (response.getText() != null) {
            return new ContentHandler(text(response.getText()));
        } else if (response.getFile() != null) {
            return new ContentHandler(file(response.getFile()));
        } else if (response.getStatus() != null) {
            return status(Integer.parseInt(response.getStatus()));
        } else if (response.getHeaders() != null) {
            Map<String, String> headers = response.getHeaders();
            Collection<ResponseHandler> collection = transform(headers.entrySet(), toHeaderResponseHandler());
            return new AndResponseHandler(collection.toArray(new ResponseHandler[collection.size()]));
        } else if (response.getUrl() != null) {
            return new ContentHandler(url(response.getUrl()));
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
