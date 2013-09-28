package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.RequestExtractor;
import com.google.common.base.Optional;
import io.netty.handler.codec.http.FullHttpRequest;

import java.nio.charset.Charset;

import static com.google.common.base.Optional.of;

public class ContentRequestExtractor implements RequestExtractor<String> {
    @Override
    public Optional<String> extract(FullHttpRequest request) {
        return of(request.content().toString(Charset.defaultCharset()));
    }
}
