package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.RequestExtractor;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

public class UriRequestExtractor implements RequestExtractor<String> {
    @Override
    public String extract(FullHttpRequest request) {
        return new QueryStringDecoder(request.getUri()).path();
    }
}
