package com.github.dreamhead.moco.runner;

import com.github.dreamhead.moco.HttpServer;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.Server;
import com.github.dreamhead.moco.SocketServer;
import com.github.dreamhead.moco.bootstrap.arg.StartArgs;
import com.github.dreamhead.moco.internal.ActualHttpServer;
import com.github.dreamhead.moco.internal.ActualSocketServer;
import com.github.dreamhead.moco.parser.HttpServerParser;
import com.github.dreamhead.moco.parser.SocketServerParser;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import java.io.InputStream;

import static com.github.dreamhead.moco.Moco.*;
import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.Iterables.toArray;

public class JsonRunner implements Runner {

    private final HttpServerParser httpParser = new HttpServerParser();
    private final SocketServerParser socketParser = new SocketServerParser();
    private final StandaloneRunner runner = new StandaloneRunner();
    private final Server server;

    private JsonRunner(Iterable<? extends RunnerSetting> settings, StartArgs startArgs) {
        this.server = createServer(settings, startArgs);
    }

    private Server createServer(Iterable<? extends RunnerSetting> settings, StartArgs startArgs) {
        if (startArgs.isSocket()) {
            return createSocketServer(settings, startArgs);
        }

        return createHttpServer(settings, startArgs);
    }

    public void run() {
        runner.run(server);
    }

    public void stop() {
        runner.stop();
    }

    private SocketServer createSocketServer(Iterable<? extends RunnerSetting> settings, StartArgs startArgs) {
        SocketServer socketServer = ActualSocketServer.createLogServer(startArgs.getPort());
        for (RunnerSetting setting : settings) {
            SocketServer parsedServer = socketParser.parseServer(setting.getStream(), startArgs.getPort(), toConfigs(setting));
            socketServer = mergeServer(socketServer, parsedServer);
        }

        return socketServer;
    }

    private SocketServer mergeServer(SocketServer socketServer, SocketServer parsedServer) {
        ActualSocketServer thisServer = (ActualSocketServer) socketServer;
        return thisServer.mergeHttpServer((ActualSocketServer)parsedServer);
    }

    private HttpServer createHttpServer(Iterable<? extends RunnerSetting> settings, StartArgs startArgs) {
        HttpServer server = createBaseHttpServer(settings, startArgs);
        server.request(by(uri("/favicon.ico"))).response(with(pathResource("favicon.png")), header("Content-Type", "image/png"));
        return server;
    }

    private HttpServer createBaseHttpServer(Iterable<? extends RunnerSetting> settings, StartArgs startArgs) {
        HttpServer server = createHttpServer(startArgs);

        for (RunnerSetting setting : settings) {
            HttpServer parsedServer = httpParser.parseServer(setting.getStream(), startArgs.getPort(), toConfigs(setting));
            server = mergeServer(server, parsedServer);
        }

        return server;
    }

    private HttpServer createHttpServer(StartArgs startArgs) {
        if (startArgs.isHttps()) {
            return ActualHttpServer.createHttpsLogServer(startArgs.getPort(), startArgs.getHttpsCertificate().get());
        }

        return ActualHttpServer.createLogServer(startArgs.getPort());
    }

    private MocoConfig[] toConfigs(RunnerSetting setting) {
        ImmutableList.Builder<MocoConfig> builder = ImmutableList.builder();

        addConfig(builder, setting.context());
        addConfig(builder, setting.fileRoot());
        addConfig(builder, setting.response());

        return toArray(builder.build(), MocoConfig.class);
    }

    private void addConfig(ImmutableList.Builder<MocoConfig> builder, Optional<MocoConfig> config) {
        if (config.isPresent()) {
            builder.add(config.get());
        }
    }

    private HttpServer mergeServer(HttpServer server, HttpServer parsedServer) {
        ActualHttpServer thisServer = (ActualHttpServer) server;
        return thisServer.mergeHttpServer((ActualHttpServer)parsedServer);
    }

    public static JsonRunner newJsonRunnerWithStreams(Iterable<? extends InputStream> streams, StartArgs startArgs) {
        return newJsonRunnerWithSetting(from(streams).transform(toRunnerSetting()), startArgs);
    }

    private static Function<InputStream, RunnerSetting> toRunnerSetting() {
        return new Function<InputStream, RunnerSetting>() {
            @Override
            public RunnerSetting apply(InputStream input) {
                return new RunnerSetting(input, null, null, null);
            }
        };
    }

    public static JsonRunner newJsonRunnerWithSetting(Iterable<? extends RunnerSetting> settings, StartArgs startArgs) {
        return new JsonRunner(settings, startArgs);
    }
}
