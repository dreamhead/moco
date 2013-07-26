package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.RequestExtractor;
import io.netty.handler.codec.http.FullHttpRequest;

public class VersionExtractor implements RequestExtractor<String> {
    @Override
    public String extract(FullHttpRequest request) {
        return request.getProtocolVersion().toString();
    }
}
