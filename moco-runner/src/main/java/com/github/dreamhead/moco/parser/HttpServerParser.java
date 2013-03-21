package com.github.dreamhead.moco.parser;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.dreamhead.moco.HttpServer;
import com.github.dreamhead.moco.parser.model.MountSetting;
import com.github.dreamhead.moco.parser.model.SessionSetting;
import com.github.dreamhead.moco.parser.model.TextContainer;
import com.github.dreamhead.moco.parser.model.TextContainerDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.github.dreamhead.moco.MocoMount.to;
import static java.lang.String.format;

public class HttpServerParser {
    private static Logger logger = LoggerFactory.getLogger(HttpServer.class);

    private final RequestMatcherParser requestMatcherParser = new DynamicRequestMatcherParser();
    private final ObjectMapper mapper = new ObjectMapper();
    private final TypeFactory factory = TypeFactory.defaultInstance();

    public HttpServerParser() {
        Module textContainerModule = new SimpleModule("TextContainerModule",
                new Version(1, 0, 0, null, null, null))
                .addDeserializer(TextContainer.class, new TextContainerDeserializer());
        mapper.registerModule(textContainerModule);
    }

    public HttpServer parseServer(InputStream is, int port) {
        List<SessionSetting> sessionSettings = readSessions(is);
        return createHttpServer(sessionSettings, port);
    }

    private List<SessionSetting> readSessions(InputStream is) {
        try {
            return mapper.readValue(is, factory.constructCollectionType(List.class, SessionSetting.class));
        } catch (UnrecognizedPropertyException e) {
            logger.info("Unrecognized field: {}", e.getMessage());
            throw new RuntimeException(format("Unrecognized field [ %s ], please check!", e.getUnrecognizedPropertyName()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpServer createHttpServer(List<SessionSetting> sessionSettings, int port) {
        HttpServer server = new HttpServer(port);
        for (SessionSetting session : sessionSettings) {
            logger.debug("Parse session: {}", session);

            if (session.isMount()) {
                MountSetting mount = session.getMount();
                server.mount(mount.getDir(), to(mount.getUri()), mount.getMountPredicates());
            } else if (session.isAnyResponse()) {
                server.response(session.getResponseHandler());
            } else if (session.isRedirectResponse()) {
                server.request(requestMatcherParser.createRequestMatcher(session.getRequest())).redirectTo(session.getRedirectTo());
            } else {
                server.request(requestMatcherParser.createRequestMatcher(session.getRequest())).response(session.getResponseHandler());
            }
        }

        return server;
    }
}
