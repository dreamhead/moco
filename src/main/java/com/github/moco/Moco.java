package com.github.moco;

public class Moco {
    public static MocoServer server(int port) {
        return new MocoServer(port);
    }

    public static void running(MocoServer server, Runnable runnable) {
        try {
            server.start();
            runnable.run();
        } finally {
            server.stop();
        }
    }
}
