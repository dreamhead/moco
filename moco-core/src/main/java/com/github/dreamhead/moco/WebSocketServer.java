package com.github.dreamhead.moco;

import com.github.dreamhead.moco.resource.Resource;
import com.github.dreamhead.moco.websocket.PongResponse;
import com.github.dreamhead.moco.websocket.WebsocketResponseSetting;

public interface WebSocketServer extends Server<WebsocketResponseSetting> {
    void connected(Resource resource);
    void connected(String text);

    PongResponse ping(String message);
    PongResponse ping(Resource message);
    PongResponse ping(RequestMatcher matcher);
}
