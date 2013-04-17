package com.github.dreamhead.moco.handler;

import com.google.common.base.Predicate;
import org.jboss.netty.handler.codec.http.HttpResponse;

import static com.google.common.collect.Iterables.any;

public class ContentTypeDetector {
    private Predicate<String> hasHeader(final String headerName) {
        return new Predicate<String>() {
            @Override
            public boolean apply(String name) {
                return name.equalsIgnoreCase(headerName);
            }
        };
    }

    public boolean hasContentType(HttpResponse response) {
        return hasHeader(response, "Content-Type");
    }

    public boolean hasHeader(HttpResponse response, String headerName) {
        return any(response.getHeaderNames(), hasHeader(headerName));
    }


}
