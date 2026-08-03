package com.github.dreamhead.moco.util;

import com.github.dreamhead.moco.HttpHeader;
import com.github.dreamhead.moco.HttpResponseSetting;
import com.github.dreamhead.moco.resource.Resource;
import io.netty.handler.codec.http.HttpResponseStatus;

import static com.github.dreamhead.moco.Moco.status;
import static com.github.dreamhead.moco.Moco.text;
import static com.github.dreamhead.moco.Moco.with;
import static com.github.dreamhead.moco.util.Preconditions.checkNotNullOrEmpty;
import static java.util.Objects.requireNonNull;

public final class RedirectDelegate {
    public HttpResponseSetting redirectTo(final HttpResponseSetting setting, final String url) {
        return this.redirectTo(setting, text(checkNotNullOrEmpty(url, "URL should not be null")));
    }

    public HttpResponseSetting redirectTo(final HttpResponseSetting setting, final Resource url) {
        return setting.response(status(HttpResponseStatus.FOUND.code()),
                with(new HttpHeader(checkNotNullOrEmpty(HttpHeaders.LOCATION, "Header name should not be null"),
                        requireNonNull(requireNonNull(url, "URL should not be null"), "Header value should not be null"))));
    }
}
