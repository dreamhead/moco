package com.github.dreamhead.moco.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.model.MessageContent;

public class JsonResponseHandler extends AbstractContentResponseHandler{
    private final ObjectMapper mapper = new ObjectMapper();
    private final Object pojo;


    public JsonResponseHandler(final Object pojo) {
        this.pojo = pojo;
    }

    @Override
    protected MessageContent responseContent(final Request request) {
        try {
            return MessageContent.content().withContent(mapper.writeValueAsBytes(pojo)).build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
