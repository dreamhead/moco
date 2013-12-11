package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.RequestExtractor;
import com.google.common.base.Optional;
import io.netty.handler.codec.http.FullHttpRequest;

import static com.google.common.base.Optional.of;

public class HttpMethodExtractor implements RequestExtractor<String> {
    @Override
    public Optional<String> extract(HttpRequest request) {
        return of(request.getMethod().toUpperCase());
    }
}
