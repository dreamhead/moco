package com.github.dreamhead.moco.matcher;

import com.github.dreamhead.moco.RequestMatcher;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;

public class PostMethodRequestMatcher implements RequestMatcher {
    @Override
    public boolean match(HttpRequest request) {
        return HttpMethod.POST.equals(request.getMethod());
    }
}
