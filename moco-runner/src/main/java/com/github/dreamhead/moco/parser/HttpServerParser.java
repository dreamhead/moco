package com.github.dreamhead.moco.parser;

import com.github.dreamhead.moco.HttpServer;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.internal.ActualHttpServer;
import com.github.dreamhead.moco.parser.model.SessionSetting;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpServerParser extends BaseParser<HttpServer> {
    private static Logger logger = LoggerFactory.getLogger(HttpServerParser.class);

    @Override
    protected HttpServer createServer(final ImmutableList<SessionSetting> sessionSettings, final Optional<Integer> port, final MocoConfig... configs) {
        HttpServer server = ActualHttpServer.createLogServer(port, configs);
        for (SessionSetting session : sessionSettings) {
            logger.debug("Parse session: {}", session);

            session.bindTo(server);
        }

        return server;
    }
}
