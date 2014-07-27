package com.github.dreamhead.moco;

public interface HttpResponseSetting extends ResponseSetting {
    HttpResponseSetting redirectTo(final String url);
}
