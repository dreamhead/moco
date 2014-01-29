package com.github.dreamhead.moco.parser;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.dreamhead.moco.HttpServer;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.internal.ActualHttpServer;
import com.github.dreamhead.moco.parser.deserializer.ProxyContainerDeserializer;
import com.github.dreamhead.moco.parser.deserializer.TextContainerDeserializer;
import com.github.dreamhead.moco.parser.model.*;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

public class HttpServerParser {
    private static Logger logger = LoggerFactory.getLogger(HttpServer.class);
    private CollectionReader reader;

    public HttpServerParser() {
        Module textContainerModule = new SimpleModule("TextContainerModule",
                new Version(1, 0, 0, null, null, null))
                .addDeserializer(TextContainer.class, new TextContainerDeserializer());
        Module proxyContainerModule = new SimpleModule("ProxyContainerModule",
                new Version(1, 0, 0, null, null, null))
                .addDeserializer(ProxyContainer.class, new ProxyContainerDeserializer());
        this.reader = new CollectionReader(textContainerModule, proxyContainerModule);
    }

    public HttpServer parseServer(InputStream is, Optional<Integer> port, MocoConfig... configs) {
        return createHttpServer(reader.read(is, SessionSetting.class), port, configs);
    }

    private HttpServer createHttpServer(ImmutableList<SessionSetting> sessionSettings, Optional<Integer> port, MocoConfig... configs) {
        HttpServer server = ActualHttpServer.createLogServer(port, configs);
        for (SessionSetting session : sessionSettings) {
            logger.debug("Parse session: {}", session);

            session.bindTo(server);
        }

        return server;
    }
}
