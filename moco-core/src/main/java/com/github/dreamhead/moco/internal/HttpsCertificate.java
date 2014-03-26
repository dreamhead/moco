package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.resource.ContentResource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class HttpsCertificate {

    private final InputStream keyStore;
    private final String keyStorePassword;
    private final String certPassword;

    public HttpsCertificate(InputStream keyStore, String keyStorePassword, String certPassword) {
        this.keyStore = keyStore;
        this.keyStorePassword = keyStorePassword;
        this.certPassword = certPassword;
    }

    public InputStream getKeyStore() {
        return keyStore;
    }

    public char[] getKeyStorePassword() {
        return keyStorePassword.toCharArray();
    }

    public char[] getCertPassword() {
        return certPassword.toCharArray();
    }

    public static HttpsCertificate certificate(ContentResource resource, String keyStorePassword, String certPassword) {
        return new HttpsCertificate(new ByteArrayInputStream(resource.readFor(null)), keyStorePassword, certPassword);
    }
}
