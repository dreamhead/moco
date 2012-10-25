package com.github.moco.setting;

import com.github.moco.RequestMatcher;
import com.github.moco.Setting;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.HttpRequest;

public class BaseSetting extends Setting {
    public BaseSetting(RequestMatcher matcher) {
        super(matcher);
    }

    public  boolean match(HttpRequest request) {
        return this.matcher.match(request);
    }

    public void handle(Channel channel) {
        this.handler.writeToResponse(channel);
    }
}
