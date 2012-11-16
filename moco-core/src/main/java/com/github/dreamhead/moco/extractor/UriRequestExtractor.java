package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.RequestExtractor;
import org.jboss.netty.handler.codec.http.HttpRequest;

public class UriRequestExtractor implements RequestExtractor {
    @Override
    public String extract(HttpRequest request) {
        String uri = request.getUri();
        int index = uri.indexOf("?");
        return index == -1 ? uri : uri.substring(0, index);
    }
}
