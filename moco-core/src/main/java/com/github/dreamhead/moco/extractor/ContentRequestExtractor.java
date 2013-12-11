package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.RequestExtractor;
import com.github.dreamhead.moco.model.LazyHttpRequest;
import com.google.common.base.Optional;
import io.netty.handler.codec.http.FullHttpRequest;

import java.nio.charset.Charset;

import static com.google.common.base.Optional.of;

public class ContentRequestExtractor implements RequestExtractor<String> {
    @Override
    public Optional<String> extract(HttpRequest request) {
        FullHttpRequest httpRequest = ((LazyHttpRequest)request).getRawRequest();
        return of(httpRequest.content().toString(Charset.defaultCharset()));
    }
}
