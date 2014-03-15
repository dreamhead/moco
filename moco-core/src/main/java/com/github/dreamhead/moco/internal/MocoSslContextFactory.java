package com.github.dreamhead.moco.internal;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.security.KeyStore;
import java.security.Security;

public class MocoSslContextFactory {

    private static final String DEFAULT_CERT = "/cert.jks";
    private static final String DEFAULT_CERT_PASSWD = "mocohttps";
    private static final String PROTOCOL = "TLS";
    private static final SSLContext SERVER_CONTEXT;
    private static final SSLContext CLIENT_CONTEXT;

    static {
        String algorithm = Security.getProperty("ssl.KeyManagerFactory.algorithm");
        if (algorithm == null) {
            algorithm = "SunX509";
        }

        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(MocoSslContextFactory.class.getResourceAsStream(DEFAULT_CERT), DEFAULT_CERT_PASSWD.toCharArray());

            KeyManagerFactory factory = KeyManagerFactory.getInstance(algorithm);
            factory.init(keyStore, DEFAULT_CERT_PASSWD.toCharArray());

            SERVER_CONTEXT = SSLContext.getInstance(PROTOCOL);
            SERVER_CONTEXT.init(factory.getKeyManagers(), null, null);
        } catch (Exception e) {
            throw new Error("Failed to initialize the server-side SSLContext", e);
        }

        try {
            CLIENT_CONTEXT = SSLContext.getInstance(PROTOCOL);
            CLIENT_CONTEXT.init(null, AnyCertificateAcceptingTrustManagerFactory.getTrustManagers(), null);
        } catch (Exception e) {
            throw new Error("Failed to initialize the client-side SSLContext", e);
        }
    }

    public static SSLContext getServerContext() {
        return SERVER_CONTEXT;
    }

    public static SSLContext getClientContext() {
        return CLIENT_CONTEXT;
    }

    private MocoSslContextFactory() {}
}
