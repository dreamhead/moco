package com.github.moco;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.HttpRequest;

public interface RequestHandler {
    void handle(HttpRequest request, Channel channel);
}
