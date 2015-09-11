package com.github.dreamhead.moco.setting;

import com.github.dreamhead.moco.HttpResponseSetting;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.internal.InternalApis;
import com.github.dreamhead.moco.matcher.AndRequestMatcher;
import com.github.dreamhead.moco.resource.Resource;
import com.github.dreamhead.moco.util.RedirectDelegate;

import static com.github.dreamhead.moco.util.Configs.configItem;
import static com.github.dreamhead.moco.util.Configs.configItems;
import static com.google.common.collect.ImmutableList.of;

public class HttpSetting extends BaseSetting<HttpResponseSetting>
        implements Setting<HttpResponseSetting>, HttpResponseSetting {
    private final RedirectDelegate delegate = new RedirectDelegate();

    public HttpSetting(final RequestMatcher matcher) {
        super(matcher);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Setting<HttpResponseSetting> apply(final MocoConfig config) {
        RequestMatcher appliedMatcher = configItem(this.matcher, config);
        if (config.isFor(MocoConfig.URI_ID) && this.matcher == appliedMatcher) {
            appliedMatcher = new AndRequestMatcher(of(appliedMatcher, InternalApis.context((String) config.apply(""))));
        }

        HttpSetting setting = new HttpSetting(appliedMatcher);
        setting.handler = configItem(this.handler, config);
        setting.eventTriggers = configItems(eventTriggers, config);
        return setting;
    }

    @Override
    public HttpResponseSetting redirectTo(final String url) {
        return delegate.redirectTo(this, url);
    }

    @Override
    public HttpResponseSetting redirectTo(final Resource url) {
        return delegate.redirectTo(this, url);
    }
}
