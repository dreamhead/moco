package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.resource.ContentResource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class HttpsCertificate {

    private final ContentResource resource;
    private final String keyStorePassword;
    private final String certPassword;

    public HttpsCertificate(ContentResource resource, String keyStorePassword, String certPassword) {
        this.resource = resource;
        this.keyStorePassword = keyStorePassword;
        this.certPassword = certPassword;
    }

    public InputStream getKeyStore() {
        return new ByteArrayInputStream(resource.readFor(null));
    }

    public char[] getKeyStorePassword() {
        return keyStorePassword.toCharArray();
    }

    public char[] getCertPassword() {
        return certPassword.toCharArray();
    }

    public static HttpsCertificate certificate(ContentResource resource, String keyStorePassword, String certPassword) {
        return new HttpsCertificate(resource, keyStorePassword, certPassword);
    }
}
