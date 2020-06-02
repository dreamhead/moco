package com.github.dreamhead.moco.websocket;

import com.github.dreamhead.moco.resource.Resource;

public interface PongResponse {
    void pong(String message);
    void pong(Resource message);
}
