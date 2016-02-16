package com.github.dreamhead.moco;

public interface RestServer extends HttpServer {
    void resource(final String name, final RestSetting setting, final RestSetting... settings);
}
