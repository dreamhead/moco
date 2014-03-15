package com.github.dreamhead.moco;

public class RemoteTestUtils {
    private static final int PORT = 12306;
    private static final String ROOT_URL = "http://localhost:";
    private static final String HTTPS_ROOT_URL = "https://localhost:";

    public static int port() {
        return PORT;
    }

    public static String root() {
        return root(PORT);
    }

    public static String httpsRoot() {
        return root(PORT, true);
    }

    public static String root(int port) {
        return root(port, false);
    }

    public static String root(int port, boolean https) {
        return (https ? HTTPS_ROOT_URL : ROOT_URL) + port;
    }

    public static String remoteUrl(String uri) {
        return root() + uri;
    }

    public static String remoteUrl(int port, String uri) {
        return root(port) + uri;
    }
}
