package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.RequestExtractor;
import io.netty.handler.codec.http.HttpRequest;

public class VersionExtractor implements RequestExtractor<String> {
    @Override
    public String extract(HttpRequest request) {
        return request.getProtocolVersion().toString();
    }
}
