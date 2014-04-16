package com.github.dreamhead.moco.bootstrap;

import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.internal.HttpsCertificate;

public class HttpsArg {
    private String filename;
    private String keystore;
    private String cert;

    public HttpsArg(String filename, String keystore, String cert) {
        this.filename = filename;
        this.keystore = keystore;
        this.cert = cert;
    }

    public HttpsCertificate getCertificate() {
        return HttpsCertificate.certificate(Moco.file(filename), keystore, cert);
    }
}
