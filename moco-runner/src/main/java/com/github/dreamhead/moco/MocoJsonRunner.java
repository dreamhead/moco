package com.github.dreamhead.moco;

import com.github.dreamhead.moco.internal.ActualHttpServer;
import com.github.dreamhead.moco.parser.HttpServerParser;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.collect.ImmutableList;

import java.io.InputStream;

import static com.github.dreamhead.moco.util.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

public final class MocoJsonRunner {
    private static final HttpServerParser PARSER = new HttpServerParser();

    public static HttpServer jsonHttpServer(final int port, final Resource resource) {
        checkArgument(port > 0, "Port must be greater than zero");
        return parseHttpServer(requireNonNull(resource, "resource should not be null"), port);
    }

    public static HttpServer jsonHttpServer(final Resource resource) {
        return parseHttpServer(requireNonNull(resource, "resource should not be null"), 0);
    }

    public static HttpsServer jsonHttpsServer(final Resource resource,
                                              final HttpsCertificate certificate) {
        requireNonNull(certificate, "Certificate should not be null");
        ActualHttpServer httpsServer = (ActualHttpServer) Moco.httpsServer(certificate);
        return httpsServer.mergeServer((ActualHttpServer) parseHttpServer(
                requireNonNull(resource, "resource should not be null"), 0));
    }

    public static HttpsServer jsonHttpsServer(final int port, final Resource resource,
                                              final HttpsCertificate certificate) {
        checkArgument(port > 0, "Port must be greater than zero");
        requireNonNull(certificate, "Certificate should not be null");
        ActualHttpServer httpsServer = (ActualHttpServer) Moco.httpsServer(port, certificate);
        return httpsServer.mergeServer((ActualHttpServer) parseHttpServer(
                 requireNonNull(resource, "resource should not be null"), port));
    }

    private static HttpServer parseHttpServer(final Resource resource, final int port) {
        return PARSER.parseServer(ImmutableList.of(toStream(resource)), port, false);
    }

    private static InputStream toStream(final Resource resource) {
        return requireNonNull(resource, "resource should not be null").readFor((Request) null).toInputStream();
    }

    private MocoJsonRunner() {
    }
}
