package com.github.dreamhead.moco.setting;

import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.Setting;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

public class BaseSetting extends Setting {
    public BaseSetting(RequestMatcher matcher) {
        super(matcher);
    }

    public boolean match(HttpRequest request) {
        return this.matcher.match(request);
    }

    public void writeToResponse(HttpRequest request, HttpResponse response) {
        this.handler.writeToResponse(request, response);
    }
}
