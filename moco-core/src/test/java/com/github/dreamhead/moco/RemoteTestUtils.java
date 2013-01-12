package com.github.dreamhead.moco;

public class RemoteTestUtils {
    private static final int PORT = 12306;
    private static final String BASE_URL = "http://localhost:" + PORT;

    public static int port() {
        return PORT;
    }

    public static String root() {
        return BASE_URL;
    }

    public static String remoteUrl(String uri) {
        return root() + uri;
    }
}
