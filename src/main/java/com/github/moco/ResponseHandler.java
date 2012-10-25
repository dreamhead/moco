package com.github.moco;

import org.jboss.netty.channel.Channel;

public interface ResponseHandler {
    void writeToResponse(Channel channel);
}
