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

    public BaseSetting apply(final MocoConfig config) {
        BaseSetting setting = new BaseSetting(this.httpServer, this.matcher.apply(config));
        setting.handler = this.handler.apply(config);
        return setting;
    }

    @Override
    protected void onResponseAttached(ResponseHandler handler) {
        httpServer.addSetting(this);
    }
}
