package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.RequestExtractor;
import org.jboss.netty.handler.codec.http.HttpRequest;

public class HttpMethodExtractor implements RequestExtractor<String> {
    @Override
    public String extract(HttpRequest request) {
        return request.getMethod().getName().toUpperCase();
    }
}
