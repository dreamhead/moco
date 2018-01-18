package com.github.dreamhead.moco;

import com.github.dreamhead.moco.resource.Resource;

public interface HttpResponseSetting extends ResponseSetting<HttpResponseSetting> {
    HttpResponseSetting redirectTo(String url);
    HttpResponseSetting redirectTo(Resource url);
}
