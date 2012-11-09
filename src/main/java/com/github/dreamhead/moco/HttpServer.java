package com.github.dreamhead.moco;

import com.github.dreamhead.moco.handler.ContentHandler;
import com.github.dreamhead.moco.matcher.GetMethodRequestMatcher;
import com.github.dreamhead.moco.matcher.PostMethodRequestMatcher;
import com.github.dreamhead.moco.setting.BaseSetting;

import java.util.ArrayList;
import java.util.List;

import static com.github.dreamhead.moco.Moco.and;
import static com.github.dreamhead.moco.Moco.or;

public class HttpServer extends ResponseSetting {
    private final int port;
    private List<BaseSetting> settings = new ArrayList<BaseSetting>();

    public HttpServer(int port) {
        this.port = port;
    }

    public Setting request(RequestMatcher matcher) {
        BaseSetting setting = new BaseSetting(matcher);
        this.settings.add(setting);
        return setting;
    }

    public Setting request(RequestMatcher... matchers) {
        return request(or(matchers));
    }

    public Setting get(RequestMatcher matcher) {
        return request(and(new GetMethodRequestMatcher(), matcher));
    }

    public Setting post(RequestMatcher matcher) {
        return request(and(new PostMethodRequestMatcher(), matcher));
    }

    public int getPort() {
        return port;
    }

    public List<BaseSetting> getSettings() {
        return settings;
    }

    public ResponseHandler getAnyResponseHandler() {
        return this.handler;
    }
}
