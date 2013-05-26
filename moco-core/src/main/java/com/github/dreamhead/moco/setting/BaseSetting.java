package com.github.dreamhead.moco.setting;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.Setting;
import com.github.dreamhead.moco.internal.ActualHttpServer;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

public class BaseSetting extends Setting {
    private final ActualHttpServer httpServer;

    public BaseSetting(ActualHttpServer httpServer, RequestMatcher matcher) {
        super(matcher);
        this.httpServer = httpServer;
    }

    public boolean match(HttpRequest request) {
        return this.matcher.match(request);
    }

    public void writeToResponse(HttpRequest request, HttpResponse response) {
        this.handler.writeToResponse(request, response);
    }

    public void apply(MocoConfig config) {
        this.matcher.apply(config);
        this.handler.apply(config);
    }

    @Override
    protected void onResponseAttached(ResponseHandler handler) {
        httpServer.addSetting(this);
    }
}
