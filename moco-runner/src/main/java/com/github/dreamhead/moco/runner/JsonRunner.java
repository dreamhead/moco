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
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;

import java.io.InputStream;

import static com.github.dreamhead.moco.Moco.asHeader;
import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.pathResource;
import static com.github.dreamhead.moco.Moco.uri;
import static com.github.dreamhead.moco.Moco.with;
import static com.github.dreamhead.moco.runner.RunnerSetting.aRunnerSetting;
import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.Iterables.toArray;

public final class JsonRunner implements Runner {

    private final HttpServerParser httpParser = new HttpServerParser();
    private final SocketServerParser socketParser = new SocketServerParser();
    private final StandaloneRunner runner = new StandaloneRunner();
    private final Server server;

    private JsonRunner(final Iterable<? extends RunnerSetting> settings, final StartArgs startArgs) {
        this.server = newServer(settings, startArgs);
    }

    private Server newServer(final Iterable<? extends RunnerSetting> settings, final StartArgs startArgs) {
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

    private SocketServer createSocketServer(final Iterable<? extends RunnerSetting> settings,
                                            final StartArgs startArgs) {
        SocketServer socketServer = ActualSocketServer.createLogServer(startArgs.getPort().or(0));
        for (RunnerSetting setting : settings) {
            SocketServer parsedServer = socketParser.parseServer(setting.getStreams(), startArgs.getPort(),
                    toConfigs(setting));
            socketServer = mergeServer(socketServer, parsedServer);
        }

        return socketServer;
    }

    private SocketServer mergeServer(final SocketServer socketServer, final SocketServer parsedServer) {
        ActualSocketServer thisServer = (ActualSocketServer) socketServer;
        return thisServer.mergeServer((ActualSocketServer) parsedServer);
    }

    private HttpServer createHttpServer(final Iterable<? extends RunnerSetting> settings, final StartArgs startArgs) {
        HttpServer targetServer = createBaseHttpServer(settings, startArgs);
        targetServer.request(by(uri("/favicon.ico"))).response(with(pathResource("favicon.png")),
                with(asHeader(HttpHeaders.CONTENT_TYPE, MediaType.PNG.toString())));
        return targetServer;
    }

    private HttpServer createBaseHttpServer(final Iterable<? extends RunnerSetting> settings,
                                            final StartArgs startArgs) {
        HttpServer targetServer = createHttpServer(startArgs);

        for (RunnerSetting setting : settings) {
            HttpServer parsedServer = httpParser.parseServer(setting.getStreams(),
                    startArgs.getPort(), toConfigs(setting));
            targetServer = mergeServer(targetServer, parsedServer);
        }

        return targetServer;
    }

    private HttpServer createHttpServer(final StartArgs startArgs) {
        if (startArgs.isHttps()) {
            return ActualHttpServer.createHttpsLogServer(startArgs.getPort().or(0), startArgs.getHttpsCertificate().get());
        }

        return ActualHttpServer.createLogServer(startArgs.getPort().or(0));
    }

    private MocoConfig[] toConfigs(final RunnerSetting setting) {
        ImmutableList.Builder<MocoConfig> builder = ImmutableList.builder();

        addConfig(builder, setting.context());
        addConfig(builder, setting.fileRoot());
        addConfig(builder, setting.request());
        addConfig(builder, setting.response());

        return toArray(builder.build(), MocoConfig.class);
    }

    private void addConfig(final ImmutableList.Builder<MocoConfig> builder, final Optional<MocoConfig> config) {
        if (config.isPresent()) {
            builder.add(config.get());
        }
    }

    private HttpServer mergeServer(final HttpServer server, final HttpServer parsedServer) {
        ActualHttpServer thisServer = (ActualHttpServer) server;
        return thisServer.mergeServer((ActualHttpServer) parsedServer);
    }

    public static JsonRunner newJsonRunnerWithStreams(final Iterable<? extends InputStream> streams,
                                                      final StartArgs startArgs) {
        return newJsonRunnerWithSetting(from(streams).transform(toRunnerSetting()), startArgs);
    }

    private static Function<InputStream, RunnerSetting> toRunnerSetting() {
        return new Function<InputStream, RunnerSetting>() {
            @Override
            public RunnerSetting apply(final InputStream input) {
                return aRunnerSetting().addStream(input).build();
            }
        };
    }

    public static JsonRunner newJsonRunnerWithSetting(final Iterable<? extends RunnerSetting> settings,
                                                      final StartArgs startArgs) {
        return new JsonRunner(settings, startArgs);
    }
}
