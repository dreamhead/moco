package com.github.dreamhead.moco.setting;

import com.github.dreamhead.moco.*;
import com.github.dreamhead.moco.internal.BaseResponseSettingConfiguration;
import com.github.dreamhead.moco.internal.InternalApis;
import com.github.dreamhead.moco.internal.SessionContext;
import com.github.dreamhead.moco.matcher.AndRequestMatcher;
import io.netty.handler.codec.http.HttpResponseStatus;

import static com.github.dreamhead.moco.Moco.header;
import static com.github.dreamhead.moco.Moco.status;
import static com.github.dreamhead.moco.util.Configs.configItem;
import static com.github.dreamhead.moco.util.Configs.configItems;
import static com.github.dreamhead.moco.util.Preconditions.checkNotNullOrEmpty;
import static com.google.common.collect.ImmutableList.of;

public class HttpSetting extends BaseResponseSettingConfiguration<HttpResponseSetting> implements Setting<HttpResponseSetting>, HttpResponseSetting {
    private final RequestMatcher matcher;

    public HttpSetting(final RequestMatcher matcher) {
        this.matcher = matcher;
    }

    @Override
    public boolean match(Request request) {
        return this.matcher.match(request) && this.handler != null;
    }

    @Override
    public void writeToResponse(SessionContext context) {
        this.handler.writeToResponse(context);
        this.fireCompleteEvent();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Setting apply(final MocoConfig config) {
        RequestMatcher appliedMatcher = configItem(this.matcher, config);
        if (config.isFor("uri") && this.matcher == appliedMatcher) {
            appliedMatcher = new AndRequestMatcher(of(appliedMatcher, InternalApis.context((String) config.apply(""))));
        }

        HttpSetting setting = new HttpSetting(appliedMatcher);
        setting.handler = configItem(this.handler, config);
        setting.eventTriggers = configItems(eventTriggers, config);
        return setting;
    }

    public void fireCompleteEvent() {
        for (MocoEventTrigger eventTrigger : eventTriggers) {
            if (eventTrigger.isFor(MocoEvent.COMPLETE)) {
                eventTrigger.fireEvent();
            }
        }
    }

    @Override
    protected HttpResponseSetting self() {
        return this;
    }

    @Override
    public HttpResponseSetting redirectTo(String url) {
        return this.response(status(HttpResponseStatus.FOUND.code()), header("Location", checkNotNullOrEmpty(url, "URL should not be null")));
    }
}
