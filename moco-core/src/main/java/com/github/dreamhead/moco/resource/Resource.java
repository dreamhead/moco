package com.github.dreamhead.moco.resource;

import com.github.dreamhead.moco.ConfigApplier;
import org.jboss.netty.handler.codec.http.HttpRequest;

public interface Resource extends ConfigApplier<Resource> {
    String id();
    byte[] asByteArray(HttpRequest request);
}
