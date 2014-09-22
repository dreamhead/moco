package com.github.dreamhead.moco;

import com.github.dreamhead.moco.parser.HttpServerParser;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.base.Optional;

import java.io.ByteArrayInputStream;

import static com.google.common.base.Optional.of;

public class MocoRunner {
    public static HttpServer jsonHttpServer(Resource resource) {
        Optional<Integer> port = Optional.absent();
        return jsonHttpServer(resource, port);
    }

    public static HttpServer jsonHttpServer(int port, Resource resource) {
        return jsonHttpServer(resource, of(port));
    }

    private static HttpServer jsonHttpServer(Resource resource, Optional<Integer> port) {
        HttpServerParser parser = new HttpServerParser();
        Optional<Request> request = Optional.absent();
        return parser.parseServer(new ByteArrayInputStream(resource.readFor(request)), port);
    }
}
