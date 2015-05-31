package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.model.MessageContent;

import static com.github.dreamhead.moco.util.Jsons.toJson;

public class JsonResponseHandler extends AbstractContentResponseHandler {
    private final Object pojo;


    public JsonResponseHandler(final Object pojo) {
        this.pojo = pojo;
    }

    @Override
    protected MessageContent responseContent(final Request request) {
        return MessageContent.content().withContent(toJson(this.pojo)).build();
    }

    @Override
    protected String getContentType(HttpRequest request) {
        return "application/json";
    }
}
