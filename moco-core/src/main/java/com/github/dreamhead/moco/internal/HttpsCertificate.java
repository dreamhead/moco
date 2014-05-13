package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.resource.ContentResource;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.Security;

import static com.github.dreamhead.moco.util.Preconditions.checkNotNullOrEmpty;
import static com.google.common.base.Preconditions.checkNotNull;

public class HttpsCertificate {

    private static final String PROTOCOL = "TLS";
    private static final String DEFAULT_ALGORITHM = "SunX509";
    private final ContentResource resource;
    private final String keyStorePassword;
    private final String certPassword;

    public HttpsCertificate(ContentResource resource, String keyStorePassword, String certPassword) {
        this.resource = resource;
        this.keyStorePassword = keyStorePassword;
        this.certPassword = certPassword;
    }

    public SSLEngine createSSLEngine() {
        return createServerContext().createSSLEngine();
    }

    private SSLContext createServerContext() {
        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(this.getKeyStore(), this.getKeyStorePassword());
            KeyManagerFactory factory = KeyManagerFactory.getInstance(getAlgorithm());
            factory.init(keyStore, this.getCertPassword());

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

    private InputStream getKeyStore() {
        return new ByteArrayInputStream(resource.readFor(null));
    }

    private char[] getKeyStorePassword() {
        return keyStorePassword.toCharArray();
    }

    private char[] getCertPassword() {
        return certPassword.toCharArray();
    }

    public static HttpsCertificate certificate(ContentResource resource, String keyStorePassword, String certPassword) {
        return new HttpsCertificate(checkNotNull(resource),
                checkNotNullOrEmpty(keyStorePassword, "Key store password should not be null"),
                checkNotNullOrEmpty(certPassword, "Cert password should not be null"));
    }
}
