package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.HttpResponseSetting;
import com.github.dreamhead.moco.RequestMatcher;
import io.netty.handler.codec.http.HttpResponseStatus;

import static com.github.dreamhead.moco.Moco.*;
import static com.github.dreamhead.moco.util.Preconditions.checkNotNullOrEmpty;

public class HttpResponseSettingConfiguration extends BaseResponseSettingConfiguration<HttpResponseSetting> implements HttpResponseSetting {
    @Override
    protected HttpResponseSetting self() {
        return this;
    }

    public HttpResponseSetting redirectTo(final String url) {
        return this.response(status(HttpResponseStatus.FOUND.code()), header("Location", checkNotNullOrEmpty(url, "URL should not be null")));
    }

    protected static RequestMatcher context(final String context) {
        return match(uri(context + ".*"));
    }
}
