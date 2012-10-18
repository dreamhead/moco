package com.github.moco.request;

import com.github.moco.MocoServer;
import org.jboss.netty.handler.codec.http.HttpRequest;

public class AnyRequestSetting extends BaseRequestSetting {
    public AnyRequestSetting(MocoServer server) {
        super(server);
    }

    public boolean isMatchAny() {
        return false;
    }

    public boolean match(HttpRequest request) {
        return true;
    }
}
