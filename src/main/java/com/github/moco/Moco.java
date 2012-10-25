package com.github.moco;

import com.github.moco.internal.MocoHttpServer;
import com.github.moco.matcher.ContentRequestMatcher;
import com.github.moco.matcher.UriRequestMatcher;

public class Moco {
    public static MocoHttpServer httpserver(int port) {
        return new MocoHttpServer(port);
    }

    public static RequestMatcher eqUri(String uri) {
        return new UriRequestMatcher(uri);
    }

    public static RequestMatcher eqContent(String content) {
        return new ContentRequestMatcher(content);
    }

    public static void running(MocoHttpServer httpServer, Runnable runnable) {
        try {
            httpServer.start();
            runnable.run();
        } finally {
            httpServer.stop();
        }
    }
}
