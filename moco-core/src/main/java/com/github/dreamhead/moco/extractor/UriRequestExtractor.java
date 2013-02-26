package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.RequestExtractor;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;

public class UriRequestExtractor implements RequestExtractor {
    @Override
    public String extract(HttpRequest request) {
        return new QueryStringDecoder(request.getUri()).getPath();
    }
}
