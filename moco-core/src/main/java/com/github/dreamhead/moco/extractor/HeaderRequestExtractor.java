package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.RequestExtractor;
import io.netty.handler.codec.http.HttpRequest;

public class HeaderRequestExtractor implements RequestExtractor<String> {
    private final String name;

    public HeaderRequestExtractor(String name) {
        this.name = name;
    }

    @Override
    public String extract(HttpRequest request) {
        return request.headers().get(name);
    }
}
