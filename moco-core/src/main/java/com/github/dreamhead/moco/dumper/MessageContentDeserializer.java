package com.github.dreamhead.moco.dumper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.github.dreamhead.moco.model.MessageContent;

import java.io.IOException;

import static com.github.dreamhead.moco.model.MessageContent.content;

public class MessageContentDeserializer extends JsonDeserializer<MessageContent> {
    @Override
    public MessageContent deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return content(jp.getText());
    }
}
