package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.RequestExtractor;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

public class UriRequestExtractor implements RequestExtractor<String> {
    @Override
    public String extract(HttpRequest request) {
        return new QueryStringDecoder(request.getUri()).path();
    }
}
