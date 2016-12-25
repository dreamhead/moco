package com.github.dreamhead.moco.helper;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public final class AnyCertificateAcceptingTrustManagerFactory {

    private static final TrustManager DUMMY_TRUST_MANAGER = new X509TrustManager() {
        @Override
        public void checkClientTrusted(final X509Certificate[] x509Certificates, final String s)
                throws CertificateException {
            // always trust
        }

        @Override
        public void checkServerTrusted(final X509Certificate[] x509Certificates, final String s)
                throws CertificateException {
            // always trust
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    };

    public static TrustManager[] getTrustManagers() {
        return new TrustManager[]{DUMMY_TRUST_MANAGER};
    }

    private AnyCertificateAcceptingTrustManagerFactory() {
    }
}
