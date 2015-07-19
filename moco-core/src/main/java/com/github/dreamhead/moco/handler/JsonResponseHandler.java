package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.model.MessageContent;
import com.google.common.net.MediaType;

import static com.github.dreamhead.moco.util.Jsons.toJson;

public class JsonResponseHandler extends AbstractContentResponseHandler {
    private final MessageContent content;

    public JsonResponseHandler(final Object pojo) {
        this.content = MessageContent.content().withContent(toJson(pojo)).build();
    }

    @Override
    protected MessageContent responseContent(final Request request) {
        return content;
    }

    @Override
    protected MediaType getContentType(HttpRequest request) {
        return MediaType.create("application", "json").withCharset(content.getCharset());
    }
}
