package com.github.dreamhead.moco;

import com.github.dreamhead.moco.resource.Resource;
import com.github.dreamhead.moco.websocket.WebsocketResponseSetting;

public interface WebSocketServer extends ResponseSetting<WebsocketResponseSetting> {
    void connected(final Resource resource);
}
