package com.github.dreamhead.moco.parser.model.websocket;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.handler.AndResponseHandler;
import com.github.dreamhead.moco.parser.model.FileContainer;
import com.github.dreamhead.moco.parser.model.TextContainer;

import java.util.ArrayList;
import java.util.List;

import static com.github.dreamhead.moco.Moco.join;
import static com.github.dreamhead.moco.Moco.with;
import static com.github.dreamhead.moco.parser.model.DynamicResponseHandlerFactory.asFileResource;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class WebsocketResponseSetting {
    private TextContainer text;
    private FileContainer file;
    private BroadcastSetting broadcast;
    private String group;

    public final ResponseHandler asResponseHandler() {
        List<ResponseHandler> handlers = new ArrayList<>();
        if (text != null) {
            handlers.add(with(this.text.asResource()));
        }

        if (file != null) {
            handlers.add(with(asFileResource("file", this.file)));
        }

        if (broadcast != null) {
            handlers.add(broadcast.asHandler());
        }

        if (group != null) {
            handlers.add(join(Moco.group(this.group)));
        }

        return AndResponseHandler.and(handlers);
    }
}
