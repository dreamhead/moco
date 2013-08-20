package com.github.dreamhead.moco.setting;

import com.github.dreamhead.moco.ConfigApplier;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.Setting;
import com.github.dreamhead.moco.matcher.AndRequestMatcher;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

import static com.github.dreamhead.moco.util.Configs.configItem;
import static com.google.common.collect.ImmutableList.of;

public class BaseSetting extends Setting implements ConfigApplier<BaseSetting> {
    public BaseSetting(RequestMatcher matcher) {
        super(matcher);
    }

    public boolean match(FullHttpRequest request) {
        return this.matcher.match(request) && this.handler != null;
    }

    public void writeToResponse(FullHttpRequest request, FullHttpResponse response) {
        this.handler.writeToResponse(request, response);
    }

    public BaseSetting apply(final MocoConfig config) {
        RequestMatcher appliedMatcher = configItem(this.matcher, config);
        if (config.isFor("uri") && this.matcher == appliedMatcher) {
            appliedMatcher = new AndRequestMatcher(of(appliedMatcher, context(config.apply(""))));
        }

        BaseSetting setting = new BaseSetting(appliedMatcher);
        setting.handler = configItem(this.handler, config);
        return setting;
    }
}
