package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.RequestExtractor;
import org.jboss.netty.handler.codec.http.HttpRequest;

public class UriRequestExtractor implements RequestExtractor {
    @Override
    public String extract(HttpRequest request) {
        return request.getUri();
    }
}
