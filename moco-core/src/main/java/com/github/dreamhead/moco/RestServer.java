package com.github.dreamhead.moco;

public interface RestServer extends HttpServer {
    void resource(String name, RestSetting setting, RestSetting... settings);
}
