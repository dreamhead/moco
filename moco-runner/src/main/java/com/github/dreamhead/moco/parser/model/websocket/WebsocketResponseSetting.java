package com.github.dreamhead.moco.parser.model.websocket;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.MocoWebSockets;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.handler.AndResponseHandler;
import com.github.dreamhead.moco.parser.model.FileContainer;
import com.github.dreamhead.moco.parser.model.TextContainer;

import java.util.ArrayList;
import java.util.List;

import static com.github.dreamhead.moco.Moco.with;
import static com.github.dreamhead.moco.parser.model.DynamicResponseHandlerFactory.asFileResource;
import static com.github.dreamhead.moco.util.Iterables.head;
import static com.github.dreamhead.moco.util.Iterables.tail;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class WebsocketResponseSetting {
    private TextContainer text;
    private FileContainer file;
    private TextContainer broadcast;

    public ResponseHandler asResponseHandler() {
        List<ResponseHandler> handlers = new ArrayList<>();
        if (text != null) {
            handlers.add(with(this.text.asResource()));
        }

        if (file != null) {
            handlers.add(with(asFileResource("file", this.file)));
        }

        if (broadcast != null) {
            handlers.add(MocoWebSockets.broadcast(broadcast.asResource()));
        }

        return AndResponseHandler.and(handlers);
    }
}
