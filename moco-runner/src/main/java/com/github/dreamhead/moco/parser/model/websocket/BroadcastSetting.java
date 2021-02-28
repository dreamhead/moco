package com.github.dreamhead.moco.parser.model.websocket;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.MocoWebSockets;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.parser.model.TextContainer;
import com.github.dreamhead.moco.resource.ContentResource;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class BroadcastSetting {
    private TextContainer content;
    private String group;

    public String asResource() {
        return null;
    }

    public ResponseHandler asHandler() {
        ContentResource resource = this.content();

        if (this.group != null) {
            return MocoWebSockets.broadcast(resource, Moco.group(this.group));
        }

        return MocoWebSockets.broadcast(resource);
    }

    private ContentResource content() {
        if (this.content != null) {
            return this.content.asResource();
        }

        throw new IllegalArgumentException("Content is required for broadcast setting");
    }
}
