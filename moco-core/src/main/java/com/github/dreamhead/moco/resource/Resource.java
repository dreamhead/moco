package com.github.dreamhead.moco.resource;

import com.github.dreamhead.moco.MocoConfig;
import org.jboss.netty.handler.codec.http.HttpRequest;

public interface Resource {
    String id();
    Resource apply(final MocoConfig config);
    byte[] asByteArray(HttpRequest request);
}
