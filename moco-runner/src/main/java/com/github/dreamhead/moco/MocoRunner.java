package com.github.dreamhead.moco;

import com.github.dreamhead.moco.parser.HttpServerParser;
import com.github.dreamhead.moco.parser.SocketServerParser;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.base.Optional;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;

public class MocoRunner {
    public static HttpServer jsonHttpServer(int port, Resource resource) {
        return jsonHttpServer(resource, of(port));
    }

    public static HttpServer jsonHttpServer(Resource resource) {
        Optional<Integer> port = Optional.absent();
        return jsonHttpServer(resource, port);
    }

    public static SocketServer jsonSocketServer(int port, Resource resource) {
        return jsonSocketServer(resource, of(port));
    }

    public static SocketServer jsonSocketServer(Resource resource) {
        Optional<Integer> port = Optional.absent();
        return jsonSocketServer(resource, port);
    }

    private static SocketServer jsonSocketServer(Resource resource, Optional<Integer> port) {
        SocketServerParser parser = new SocketServerParser();
        return parser.parseServer(toStream(resource), port);
    }

    private static HttpServer jsonHttpServer(Resource resource, Optional<Integer> port) {
        HttpServerParser parser = new HttpServerParser();
        return parser.parseServer(toStream(resource), port);
    }

    private static InputStream toStream(Resource resource) {
        Optional<Request> request = Optional.absent();
        return new ByteArrayInputStream(resource.readFor(request));
    }
}
