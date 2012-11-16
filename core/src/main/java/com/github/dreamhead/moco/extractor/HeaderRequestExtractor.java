package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.RequestExtractor;
import org.jboss.netty.handler.codec.http.HttpRequest;

public class HeaderRequestExtractor implements RequestExtractor {
    private String header;

    public HeaderRequestExtractor(String header) {
        this.header = header;
    }

    @Override
    public String extract(HttpRequest request) {
        return request.getHeader(header);
    }
}
