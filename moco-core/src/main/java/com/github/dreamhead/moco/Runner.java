package com.github.dreamhead.moco;

import com.github.dreamhead.moco.internal.ActualHttpServer;
import com.github.dreamhead.moco.internal.HttpsCertificate;
import com.github.dreamhead.moco.internal.MocoHttpServer;
import com.github.dreamhead.moco.internal.MocoHttpsServer;

public abstract class Runner {
    public static void running(HttpServer httpServer, Runnable runnable) throws Exception {
        Runner server = runner(httpServer);
        try {
            server.start();
            runnable.run();
        } finally {
            server.stop();
        }
    }

    /**
     * Deprecated in favor of {@link com.github.dreamhead.moco.Runner#httpRunner(HttpServer)}.
     */
    @Deprecated
    public static Runner runner(HttpServer httpServer) {
        return httpRunner(httpServer);
    }

    public static Runner httpRunner(HttpServer httpServer) {
        return new MocoHttpServer((ActualHttpServer)httpServer);
    }

    public static Runner httpsRunner(HttpServer httpServer, HttpsCertificate certificate) {
        return new MocoHttpsServer((ActualHttpServer)httpServer, certificate);
    }

    public abstract void start();
    public abstract void stop();
}
