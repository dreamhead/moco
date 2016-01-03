package com.github.dreamhead.moco;

import com.github.dreamhead.moco.rest.HttpMethod;

public interface RestSetting {
    boolean isFor(HttpMethod method);
}
