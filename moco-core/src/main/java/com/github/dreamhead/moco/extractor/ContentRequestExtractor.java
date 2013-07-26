package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.RequestExtractor;
import io.netty.handler.codec.http.FullHttpRequest;

import java.nio.charset.Charset;

public class ContentRequestExtractor implements RequestExtractor<String> {
    @Override
    public String extract(FullHttpRequest request) {
        return request.content().toString(Charset.defaultCharset());
    }
}
