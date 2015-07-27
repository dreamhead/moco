package com.github.dreamhead.moco;

import com.github.dreamhead.moco.resource.Resource;

public interface HttpResponseSetting extends ResponseSetting<HttpResponseSetting> {
    HttpResponseSetting redirectTo(final String url);
    HttpResponseSetting redirectTo(final Resource url);
}
