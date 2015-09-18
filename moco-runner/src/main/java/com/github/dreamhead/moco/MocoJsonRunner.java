package com.github.dreamhead.moco;

import com.github.dreamhead.moco.parser.HttpServerParser;
import com.github.dreamhead.moco.parser.SocketServerParser;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.base.Optional;

import java.io.InputStream;

import static com.google.common.base.Optional.of;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class MocoJsonRunner {
    public static HttpServer jsonHttpServer(final int port, final Resource resource) {
        checkArgument(port > 0, "Port must be greater than zero");
        return jsonHttpServer(checkNotNull(resource, "resource should not be null"), of(port));
    }

    public static HttpServer jsonHttpServer(final Resource resource) {
        Optional<Integer> port = Optional.absent();
        return jsonHttpServer(checkNotNull(resource, "resource should not be null"), port);
    }

    public static SocketServer jsonSocketServer(final int port, final Resource resource) {
        checkArgument(port > 0, "Port must be greater than zero");
        return jsonSocketServer(checkNotNull(resource, "resource should not be null"), of(port));
    }

    public static SocketServer jsonSocketServer(final Resource resource) {
        Optional<Integer> port = Optional.absent();
        return jsonSocketServer(checkNotNull(resource, "resource should not be null"), port);
    }

    private static SocketServer jsonSocketServer(final Resource resource, final Optional<Integer> port) {
        SocketServerParser parser = new SocketServerParser();
        return parser.parseServer(toStream(checkNotNull(resource, "resource should not be null")), port);
    }

    private static HttpServer jsonHttpServer(final Resource resource, final Optional<Integer> port) {
        HttpServerParser parser = new HttpServerParser();
        return parser.parseServer(toStream(resource), port);
    }

    private static InputStream toStream(final Resource resource) {
        Optional<Request> request = Optional.absent();
        return checkNotNull(resource, "resource should not be null").readFor(request).toInputStream();
    }

    private MocoJsonRunner() {
    }
}
