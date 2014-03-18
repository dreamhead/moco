package com.github.dreamhead.moco.internal;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.security.KeyStore;
import java.security.Security;

public class MocoSslContextFactory {

    private static final String PROTOCOL = "TLS";
    private static final String DEFAULT_ALGORITHM = "SunX509";

    public static SSLContext createServerContext(HttpsCertificate certificate) {
        String algorithm = Security.getProperty("ssl.KeyManagerFactory.algorithm");
        if (algorithm == null) {
            algorithm = DEFAULT_ALGORITHM;
        }

        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(certificate.getKeyStore(), certificate.getKeyStorePassword().toCharArray());

            KeyManagerFactory factory = KeyManagerFactory.getInstance(algorithm);
            factory.init(keyStore, certificate.getCertPassword().toCharArray());

            SSLContext serverContext = SSLContext.getInstance(PROTOCOL);
            serverContext.init(factory.getKeyManagers(), null, null);
            return serverContext;
        } catch (Exception e) {
            throw new Error("Failed to initialize the server-side SSLContext", e);
        }
    }

    public static SSLContext createClientContext() {
        try {
            SSLContext clientContext = SSLContext.getInstance(PROTOCOL);
            clientContext.init(null, AnyCertificateAcceptingTrustManagerFactory.getTrustManagers(), null);
            return clientContext;
        } catch (Exception e) {
            throw new Error("Failed to initialize the client-side SSLContext", e);
        }
    }

    private MocoSslContextFactory() {}
}
