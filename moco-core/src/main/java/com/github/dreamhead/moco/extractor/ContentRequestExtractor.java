package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.RequestExtractor;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;

import java.nio.charset.Charset;

public class ContentRequestExtractor implements RequestExtractor<String> {
    @Override
    public String extract(HttpRequest request) {
        if (request instanceof HttpContent) {
            HttpContent content = (HttpContent)request;
            return content.content().toString(Charset.defaultCharset());
        }

        return null;
    }
}
