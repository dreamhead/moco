package com.github.dreamhead.moco;

import com.github.dreamhead.moco.rest.RestSetting;

public interface RestServer extends HttpServer {
    void resource(final String name, final RestSetting... settings);
}
