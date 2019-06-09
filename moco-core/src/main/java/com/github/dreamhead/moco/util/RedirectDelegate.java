package com.github.dreamhead.moco.util;

import com.github.dreamhead.moco.HttpResponseSetting;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.net.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;

import static com.github.dreamhead.moco.Moco.asHeader;
import static com.github.dreamhead.moco.Moco.status;
import static com.github.dreamhead.moco.Moco.text;
import static com.github.dreamhead.moco.Moco.with;
import static com.github.dreamhead.moco.util.Preconditions.checkNotNullOrEmpty;
import static com.google.common.base.Preconditions.checkNotNull;

public final class RedirectDelegate {
    public HttpResponseSetting redirectTo(final HttpResponseSetting setting, final String url) {
        return this.redirectTo(setting, text(checkNotNullOrEmpty(url, "URL should not be null")));
    }

    public HttpResponseSetting redirectTo(final HttpResponseSetting setting, final Resource url) {
        return setting.response(status(HttpResponseStatus.FOUND.code()),
                with(asHeader(HttpHeaders.LOCATION, checkNotNull(url, "URL should not be null"))));
    }
}
