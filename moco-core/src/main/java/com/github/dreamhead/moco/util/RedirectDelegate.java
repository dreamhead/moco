package com.github.dreamhead.moco.util;

import com.github.dreamhead.moco.HttpResponseSetting;
import com.google.common.net.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;

import static com.github.dreamhead.moco.Moco.header;
import static com.github.dreamhead.moco.Moco.status;
import static com.github.dreamhead.moco.util.Preconditions.checkNotNullOrEmpty;

public class RedirectDelegate {
    public HttpResponseSetting redirectTo(final HttpResponseSetting setting, final String url) {
        return setting.response(status(HttpResponseStatus.FOUND.code()), header(HttpHeaders.LOCATION, checkNotNullOrEmpty(url, "URL should not be null")));
    }
}
