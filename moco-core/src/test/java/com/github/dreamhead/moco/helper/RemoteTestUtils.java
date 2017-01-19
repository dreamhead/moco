package com.github.dreamhead.moco.helper;

public final class RemoteTestUtils {
    private static final int PORT = 12306;
    private static final String ROOT_URL = "http://localhost:";
    private static final String HTTPS_ROOT_URL = "https://localhost:";
    private static final String LOCALHOST = "localhost";

    public static int port() {
        return PORT;
    }

    public static String root() {
        return root(PORT);
    }

    public static String httpsRoot() {
        return root(PORT, true);
    }

    public static String httpsRoot(final int port) {
        return root(port, true);
    }

    public static String root(final int port) {
        return root(port, false);
    }

    private static String root(final int port, final boolean https) {
        return (https ? HTTPS_ROOT_URL : ROOT_URL) + port;
    }

    public static String remoteUrl(final String uri) {
        return root() + uri;
    }

    public static String remoteUrl(final int port, final String uri) {
        return root(port) + uri;
    }

    public static String remoteHttpsUrl(final String uri) {
        return httpsRoot() + uri;
    }

    public static String local() {
        return LOCALHOST;
    }

    private RemoteTestUtils() {
    }
}
