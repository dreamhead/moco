package com.github.dreamhead.moco.resource;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.util.Cookies;
import org.jboss.netty.handler.codec.http.HttpRequest;

public class HeaderResource implements Resource {
    private final Cookies cookies = new Cookies();
    private final String key;
    private final Resource resource;

    public HeaderResource(String key, Resource resource) {
        this.key = key;
        this.resource = resource;
    }

    @Override
    public String id() {
        return "header";
    }

    @Override
    public Resource apply(MocoConfig config) {
        if (config.isFor(resource.id())) {
           return new HeaderResource(key, resource.apply(config));
        }

        return this;
    }

    @Override
    public byte[] asByteArray(HttpRequest request) {
        return cookies.encodeCookie(key, new String(resource.asByteArray(request))).getBytes();
    }
}
