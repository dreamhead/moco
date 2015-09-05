package com.github.dreamhead.moco.junit;

import com.github.dreamhead.moco.HttpServer;
import com.github.dreamhead.moco.Runner;
import org.junit.rules.ExternalResource;

import static com.github.dreamhead.moco.Moco.file;
import static com.github.dreamhead.moco.MocoJsonRunner.jsonHttpServer;

public final class MocoJunitRunner extends ExternalResource {
    private final Runner runner;

    private MocoJunitRunner(final Runner runner) {
        this.runner = runner;
    }

    public static MocoJunitRunner jsonHttpRunner(final int port, final String filename) {
        return new MocoJunitRunner(Runner.runner(jsonHttpServer(port, file(filename))));
    }

    public static MocoJunitRunner httpRunner(final HttpServer server) {
        return new MocoJunitRunner(Runner.runner(server));
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
