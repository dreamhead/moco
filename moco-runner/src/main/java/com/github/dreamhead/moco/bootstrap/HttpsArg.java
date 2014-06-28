package com.github.dreamhead.moco.bootstrap;

import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.HttpsCertificate;

public class HttpsArg {
    private final String filename;
    private final String keystore;
    private final String cert;

    public HttpsArg(final String filename, final String keystore, final String cert) {
        this.filename = filename;
        this.keystore = keystore;
        this.cert = cert;
    }

    public HttpsCertificate getCertificate() {
        return HttpsCertificate.certificate(Moco.file(filename), keystore, cert);
    }
}
