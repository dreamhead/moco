package com.github.dreamhead.moco;

import com.github.dreamhead.moco.parser.HttpServerParser;
import com.github.dreamhead.moco.parser.SocketServerParser;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.base.Optional;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static com.google.common.base.Optional.of;

public class MocoRunner {
    public static HttpServer jsonHttpServer(final int port, final Resource resource) {
        return jsonHttpServer(resource, of(port));
    }

    public static HttpServer jsonHttpServer(final Resource resource) {
        Optional<Integer> port = Optional.absent();
        return jsonHttpServer(resource, port);
    }

    public static SocketServer jsonSocketServer(final int port, final Resource resource) {
        return jsonSocketServer(resource, of(port));
    }

    public static SocketServer jsonSocketServer(final Resource resource) {
        Optional<Integer> port = Optional.absent();
        return jsonSocketServer(resource, port);
    }

    private static SocketServer jsonSocketServer(final Resource resource, final Optional<Integer> port) {
        SocketServerParser parser = new SocketServerParser();
        return parser.parseServer(toStream(resource), port);
    }

    private static HttpServer jsonHttpServer(final Resource resource, final Optional<Integer> port) {
        HttpServerParser parser = new HttpServerParser();
        return parser.parseServer(toStream(resource), port);
    }

    private static InputStream toStream(final Resource resource) {
        Optional<Request> request = Optional.absent();
        return new ByteArrayInputStream(resource.readFor(request));
    }

    private MocoRunner() {}
}
