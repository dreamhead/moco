package com.github.dreamhead.moco.resource;

import com.github.dreamhead.moco.MocoConfig;
import org.jboss.netty.handler.codec.http.HttpRequest;

public class TextResource implements ContentResource {
    private final String text;

    public TextResource(String text) {
        this.text = text;
    }

    @Override
    public String id() {
        return "text";
    }

    @Override
    public byte[] asByteArray(HttpRequest request) {
        return text.getBytes();
    }

    @Override
    public Resource apply(final MocoConfig config) {
        return this;
    }

    @Override
    public String getContentType() {
        return "text/plain; charset=UTF-8";
    }
}
