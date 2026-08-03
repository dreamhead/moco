package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.HttpsCertificate;
import io.netty.handler.ssl.ApplicationProtocolConfig;
import io.netty.handler.ssl.ApplicationProtocolNames;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

public class AlpnSslHandler extends SslHandler {
    private static final String[] PROTOCOLS = {ApplicationProtocolNames.HTTP_2, ApplicationProtocolNames.HTTP_1_1};
    private final SslContext sslContext;

    public AlpnSslHandler(final HttpsCertificate certificate) {
        this(createSslContext(certificate));
    }

    private AlpnSslHandler(final SslContext sslContext) {
        super(createSSLEngine(sslContext));
        this.sslContext = sslContext;
    }

    private static SSLEngine createSSLEngine(final SslContext sslContext) {
        SSLEngine engine = sslContext.newEngine(io.netty.buffer.ByteBufAllocator.DEFAULT);
        engine.setUseClientMode(false);
        return engine;
    }

    public SslContext getSslContext() {
        return sslContext;
    }

    private static SslContext createSslContext(final HttpsCertificate certificate) {
        try {
            KeyStore keyStore = loadKeyStore(certificate);

            SslContextBuilder builder = SslContextBuilder.forServer(
                    getKey(keyStore, certificate),
                    getCertificateChain(keyStore)
            );

            ApplicationProtocolConfig alpnConfig = new ApplicationProtocolConfig(
                    ApplicationProtocolConfig.Protocol.ALPN,
                    ApplicationProtocolConfig.SelectorFailureBehavior.NO_ADVERTISE,
                    ApplicationProtocolConfig.SelectedListenerFailureBehavior.ACCEPT,
                    PROTOCOLS
            );

            return builder.applicationProtocolConfig(alpnConfig).build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create SSL context with ALPN", e);
        }
    }

    private static KeyStore loadKeyStore(final HttpsCertificate certificate) throws Exception {
        try (InputStream is = certificate.getResource()
                .readFor((com.github.dreamhead.moco.Request) null).toInputStream()) {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(is, certificate.getKeyStorePassword());
            return keyStore;
        }
    }

    private static java.security.PrivateKey getKey(final KeyStore keyStore, final HttpsCertificate certificate) throws Exception {
        String alias = keyStore.aliases().nextElement();
        return (PrivateKey) keyStore.getKey(alias, certificate.getCertPassword());
    }

    private static X509Certificate[] getCertificateChain(final KeyStore keyStore) throws Exception {
        String alias = keyStore.aliases().nextElement();
        Certificate[] certs = keyStore.getCertificateChain(alias);
        X509Certificate[] x509Certificates = new X509Certificate[certs.length];
        for (int i = 0; i < certs.length; i++) {
            x509Certificates[i] = (X509Certificate) certs[i];
        }
        return x509Certificates;
    }
}
