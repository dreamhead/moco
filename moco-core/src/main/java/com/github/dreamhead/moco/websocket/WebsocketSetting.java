package com.github.dreamhead.moco.websocket;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.internal.InternalApis;
import com.github.dreamhead.moco.matcher.AndRequestMatcher;
import com.github.dreamhead.moco.setting.BaseSetting;
import com.github.dreamhead.moco.setting.Setting;

import static com.github.dreamhead.moco.util.Configs.configItem;
import static com.google.common.collect.ImmutableList.of;

public class WebsocketSetting extends BaseSetting<WebsocketResponseSetting>
        implements Setting<WebsocketResponseSetting> {
    protected WebsocketSetting(final RequestMatcher matcher) {
        super(matcher);
    }

    @Override
    protected final BaseSetting<WebsocketResponseSetting> createSetting(final RequestMatcher matcher) {
        return new WebsocketSetting(matcher);
    }

    @Override
    protected final RequestMatcher configMatcher(final RequestMatcher matcher, final MocoConfig config) {
        RequestMatcher appliedMatcher = configItem(matcher, config);
        if (config.isFor(MocoConfig.URI_ID) && matcher == appliedMatcher) {
            return new AndRequestMatcher(of(appliedMatcher, InternalApis.context((String) config.apply(""))));
        }

        return appliedMatcher;
    }
}
