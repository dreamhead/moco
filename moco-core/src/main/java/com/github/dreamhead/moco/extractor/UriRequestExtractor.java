package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.RequestExtractor;
import com.google.common.base.Optional;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import static com.google.common.base.Optional.of;

public class UriRequestExtractor implements RequestExtractor<String> {
    @Override
    public Optional<String> extract(FullHttpRequest request) {
        return of(new QueryStringDecoder(request.getUri()).path());
    }
}
