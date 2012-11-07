package com.github.dreamhead.moco.matcher;

import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.model.Header;
import org.jboss.netty.handler.codec.http.HttpRequest;

public class HeaderRequestMatcher implements RequestMatcher {
    private final Header header;
    private final String expected;

    public HeaderRequestMatcher(Header header, String expected) {
        this.header = header;
        this.expected = expected;
    }

    @Override
    public boolean match(HttpRequest request) {
        return expected.equals(request.getHeader(header.getHeader()));
    }
}
