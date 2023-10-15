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
import com.google.common.collect.ImmutableList;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;

import java.io.InputStream;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.header;
import static com.github.dreamhead.moco.Moco.pathResource;
import static com.github.dreamhead.moco.Moco.uri;
import static com.github.dreamhead.moco.Moco.with;
import static com.github.dreamhead.moco.runner.RunnerSetting.aRunnerSetting;
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
        int port = startArgs.getPort().orElse(0);

        SocketServer socketServer = ActualSocketServer.createSocketServer(port, startArgs.isQuiet());

        for (RunnerSetting setting : settings) {
            SocketServer parsedServer = socketParser.parseServer(setting.getStreams(), port, startArgs.isQuiet(),
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
                with(header(HttpHeaders.CONTENT_TYPE, MediaType.PNG.toString())));
        return targetServer;
    }

    private HttpServer createBaseHttpServer(final Iterable<? extends RunnerSetting> settings,
                                            final StartArgs startArgs) {
        HttpServer targetServer = createHttpServer(startArgs);

        for (RunnerSetting setting : settings) {
            HttpServer parsedServer = httpParser.parseServer(setting.getStreams(),
                    startArgs.getPort().orElse(0), startArgs.isQuiet(), toConfigs(setting));
            targetServer = mergeServer(targetServer, parsedServer);
        }

        return targetServer;
    }

    private HttpServer createHttpServer(final StartArgs startArgs) {
        final int port = startArgs.getPort().orElse(0);

        Optional<Integer> contentLength = startArgs.getContentLength();
        if (contentLength.isPresent()) {
            if (startArgs.isHttps()) {
                return ActualHttpServer.createHttpsServer(port, startArgs.getHttpsCertificate().get(),
                        startArgs.isQuiet(), contentLength.get());
            }

            return ActualHttpServer.createHttpServer(port, startArgs.isQuiet(), contentLength.get());
        }

        return doCreateHttpServer(startArgs, port);
    }

    private static ActualHttpServer doCreateHttpServer(final StartArgs startArgs, final int port) {
        if (startArgs.isHttps()) {
            return ActualHttpServer.createHttpsServer(port, startArgs.isQuiet(), startArgs.getHttpsCertificate().get());
        }

        return ActualHttpServer.createHttpServer(port, startArgs.isQuiet());
    }

    private MocoConfig[] toConfigs(final RunnerSetting setting) {
        ImmutableList.Builder<MocoConfig> builder = ImmutableList.builder();

        setting.context().ifPresent(builder::add);
        setting.fileRoot().ifPresent(builder::add);
        setting.request().ifPresent(builder::add);
        setting.response().ifPresent(builder::add);

        return toArray(builder.build(), MocoConfig.class);
    }

    private HttpServer mergeServer(final HttpServer server, final HttpServer parsedServer) {
        ActualHttpServer thisServer = (ActualHttpServer) server;
        return thisServer.mergeServer((ActualHttpServer) parsedServer);
    }

    public static JsonRunner newJsonRunnerWithStreams(final Iterable<? extends InputStream> streams,
                                                      final StartArgs startArgs) {

        return newJsonRunnerWithSetting(StreamSupport.stream(streams.spliterator(), false)
                .map(input -> aRunnerSetting().addStream(input).build())
                .collect(Collectors.toList()), startArgs);
    }

    public static JsonRunner newJsonRunnerWithSetting(final Iterable<? extends RunnerSetting> settings,
                                                      final StartArgs startArgs) {
        return new JsonRunner(settings, startArgs);
    }
}
