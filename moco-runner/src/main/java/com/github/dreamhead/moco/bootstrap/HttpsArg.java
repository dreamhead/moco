package com.github.dreamhead.moco.bootstrap;

public class HttpsArg {
    private String filename;
    private String keystore;
    private String cert;

    public HttpsArg(String filename, String keystore, String cert) {
        this.filename = filename;
        this.keystore = keystore;
        this.cert = cert;
    }
}
