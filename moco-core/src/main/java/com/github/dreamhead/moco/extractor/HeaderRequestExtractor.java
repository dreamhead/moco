package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.RequestExtractor;
import org.jboss.netty.handler.codec.http.HttpRequest;

public class HeaderRequestExtractor implements RequestExtractor {
    private final String name;

    public HeaderRequestExtractor(String name) {
        this.name = name;
    }

    @Override
    public String extract(HttpRequest request) {
        return request.getHeader(name);
    }
}
