package com.github.dreamhead.moco.setting;

import com.github.dreamhead.moco.HttpResponseSetting;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.internal.InternalApis;
import com.github.dreamhead.moco.matcher.AndRequestMatcher;
import com.github.dreamhead.moco.resource.Resource;
import com.github.dreamhead.moco.util.RedirectDelegate;

import static com.github.dreamhead.moco.util.Configs.configItem;
import static com.google.common.collect.ImmutableList.of;

public final class HttpSetting extends BaseSetting<HttpResponseSetting>
        implements Setting<HttpResponseSetting>, HttpResponseSetting {
    private final RedirectDelegate delegate = new RedirectDelegate();

    public HttpSetting(final RequestMatcher matcher) {
        super(matcher);
    }

    @Override
    protected BaseSetting<HttpResponseSetting> createSetting(final RequestMatcher matcher) {
        return new HttpSetting(matcher);
    }

    @SuppressWarnings("unchecked")
    protected RequestMatcher configMatcher(final RequestMatcher matcher, final MocoConfig config) {
        RequestMatcher appliedMatcher = configItem(matcher, config);
        if (config.isFor(MocoConfig.URI_ID) && matcher == appliedMatcher) {
            return new AndRequestMatcher(of(appliedMatcher, InternalApis.context((String) config.apply(""))));
        }

        return appliedMatcher;
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
