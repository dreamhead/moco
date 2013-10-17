package com.github.dreamhead.moco;

public class RemoteTestUtils {
    private static final int PORT = 12306;
    private static final String ROOT_URL = "http://localhost:";

    public static int port() {
        return PORT;
    }

    public static String root() {
        return root(PORT);
    }

    public static String root(int port) {
        return ROOT_URL + port;
    }

    public static String remoteUrl(String uri) {
        return root() + uri;
    }

    public static String remoteUrl(int port, String uri) {
        return root(port) + uri;
    }
}
