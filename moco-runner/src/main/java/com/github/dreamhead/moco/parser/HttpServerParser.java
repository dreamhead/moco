package com.github.dreamhead.moco.parser;

import com.github.dreamhead.moco.HttpServer;

import java.util.List;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.internal.ActualHttpServer;
import com.github.dreamhead.moco.parser.model.SessionSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HttpServerParser extends BaseParser<HttpServer> {
    private static Logger logger = LoggerFactory.getLogger(HttpServerParser.class);

    @Override
    protected HttpServer createServer(final List<SessionSetting> sessionSettings,
                                      final int port,
                                      final boolean quiet,
                                      final MocoConfig... configs) {
        ActualHttpServer targetServer = ActualHttpServer.createHttpServer(port, quiet, configs);

        for (SessionSetting session : sessionSettings) {
            logger.debug("Parse session: {}", session);
            targetServer = targetServer.mergeServer(session.newHttpServer(port, quiet, configs));
        }

        return targetServer;
    }

}
