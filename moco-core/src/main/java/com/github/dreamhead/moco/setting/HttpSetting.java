package com.github.dreamhead.moco.setting;

import com.github.dreamhead.moco.HttpResponseSetting;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.internal.InternalApis;
import com.github.dreamhead.moco.matcher.AndRequestMatcher;
import com.google.common.net.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;

import static com.github.dreamhead.moco.Moco.header;
import static com.github.dreamhead.moco.Moco.status;
import static com.github.dreamhead.moco.util.Configs.configItem;
import static com.github.dreamhead.moco.util.Configs.configItems;
import static com.github.dreamhead.moco.util.Preconditions.checkNotNullOrEmpty;
import static com.google.common.collect.ImmutableList.of;

public class HttpSetting extends BaseSetting<HttpResponseSetting> implements Setting<HttpResponseSetting>, HttpResponseSetting {

    public HttpSetting(final RequestMatcher matcher) {
        super(matcher);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Setting<HttpResponseSetting> apply(final MocoConfig config) {
        RequestMatcher appliedMatcher = configItem(this.matcher, config);
        if (config.isFor("uri") && this.matcher == appliedMatcher) {
            appliedMatcher = new AndRequestMatcher(of(appliedMatcher, InternalApis.context((String) config.apply(""))));
        }

        HttpSetting setting = new HttpSetting(appliedMatcher);
        setting.handler = configItem(this.handler, config);
        setting.eventTriggers = configItems(eventTriggers, config);
        return setting;
    }

    @Override
    protected HttpResponseSetting self() {
        return this;
    }

    @Override
    public HttpResponseSetting redirectTo(final String url) {
        return this.response(status(HttpResponseStatus.FOUND.code()), header(HttpHeaders.LOCATION, checkNotNullOrEmpty(url, "URL should not be null")));
    }
}
