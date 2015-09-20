package com.github.dreamhead.moco.junit;

import com.github.dreamhead.moco.HttpServer;
import com.github.dreamhead.moco.Runner;
import com.github.dreamhead.moco.SocketServer;
import org.junit.rules.ExternalResource;

import static com.github.dreamhead.moco.Moco.file;
import static com.github.dreamhead.moco.MocoJsonRunner.jsonHttpServer;
import static com.github.dreamhead.moco.MocoJsonRunner.jsonSocketServer;
import static com.github.dreamhead.moco.Runner.runner;

public final class MocoJunitRunner extends ExternalResource {
    private final Runner runner;

    private MocoJunitRunner(final Runner runner) {
        this.runner = runner;
    }

    public static MocoJunitRunner jsonHttpRunner(final int port, final String filename) {
        return new MocoJunitRunner(runner(jsonHttpServer(port, file(filename))));
    }

    public static MocoJunitRunner httpRunner(final HttpServer server) {
        return new MocoJunitRunner(runner(server));
    }

    public static MocoJunitRunner jsonSocketRunner(final int port, final String filename) {
        return new MocoJunitRunner(runner(jsonSocketServer(port, file(filename))));
    }

    public static MocoJunitRunner socketRunner(final SocketServer server) {
        return new MocoJunitRunner(runner(server));
    }

    @Override
    protected void before() throws Throwable {
        runner.start();
    }

    @Override
    protected void after() {
        if (runner != null) {
            runner.stop();
        }
    }
}
