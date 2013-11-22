package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.RequestExtractor;
import com.google.common.base.Optional;
import io.netty.handler.codec.http.FullHttpRequest;

public class HeaderRequestExtractor implements RequestExtractor<String> {
    private final String name;

    public HeaderRequestExtractor(final String name) {
        this.name = name;
    }

    @Override
    public Optional<String> extract(FullHttpRequest request) {
        return Optional.fromNullable(request.headers().get(name));
    }
}
