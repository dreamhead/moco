package com.github.dreamhead.moco.matcher;

import com.github.dreamhead.moco.RequestMatcher;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;

public class HttpMethodRequestMatcher implements RequestMatcher {
    private HttpMethod method;

    public HttpMethodRequestMatcher(HttpMethod method) {
        this.method = method;
    }

    @Override
    public boolean match(HttpRequest request) {
        return method.equals(request.getMethod());
    }
}
