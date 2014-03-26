package com.github.dreamhead.moco.internal;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.security.KeyStore;
import java.security.Security;

public class MocoSslContextFactory {
    private static final String PROTOCOL = "TLS";
    private static final String DEFAULT_ALGORITHM = "SunX509";

    public static SSLContext createServerContext(HttpsCertificate certificate) {

        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(certificate.getKeyStore(), certificate.getKeyStorePassword());
            KeyManagerFactory factory = KeyManagerFactory.getInstance(getAlgorithm());
            factory.init(keyStore, certificate.getCertPassword());

            SSLContext serverContext = SSLContext.getInstance(PROTOCOL);
            serverContext.init(factory.getKeyManagers(), null, null);
            return serverContext;
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize the server-side SSLContext", e);
        }
    }

    private static String getAlgorithm() {
        String algorithm = Security.getProperty("ssl.KeyManagerFactory.algorithm");
        if (algorithm == null) {
            return DEFAULT_ALGORITHM;
        }

        return algorithm;
    }

    public static SSLContext createClientContext() {
        try {
            SSLContext clientContext = SSLContext.getInstance(PROTOCOL);
            clientContext.init(null, AnyCertificateAcceptingTrustManagerFactory.getTrustManagers(), null);
            return clientContext;
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize the client-side SSLContext", e);
        }
    }

    private MocoSslContextFactory() {}
}
