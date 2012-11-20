package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.RequestExtractor;
import org.jboss.netty.handler.codec.http.HttpRequest;

import java.nio.charset.Charset;

public class ContentRequestExtractor implements RequestExtractor {
    @Override
    public String extract(HttpRequest request) {
        return request.getContent().toString(Charset.defaultCharset());
    }
}
