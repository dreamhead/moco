package com.github.dreamhead.moco.internal;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public String getCertPassword() {
        return certPassword;
    }

    public static HttpsCertificate classpathCertificate(String fileName, String keyStorePassword, String certPassword) {
        return new HttpsCertificate(HttpsCertificate.class.getResourceAsStream(fileName), keyStorePassword, certPassword);
    }

    public static HttpsCertificate fileCertificate(String fileName, String keyStorePassword, String certPassword) {
        try {
            return new HttpsCertificate(new FileInputStream(fileName), keyStorePassword, certPassword);
        } catch (FileNotFoundException e) {
            throw new Error("Cannot load certificate from file " + fileName);
        }
    }
}
