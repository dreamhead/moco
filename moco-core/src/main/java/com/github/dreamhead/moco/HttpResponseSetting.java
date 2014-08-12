package com.github.dreamhead.moco;

public interface HttpResponseSetting extends ResponseSetting<HttpResponseSetting> {
    HttpResponseSetting redirectTo(final String url);
}
