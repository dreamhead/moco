package com.github.dreamhead.moco.dumper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.github.dreamhead.moco.model.MessageContent;

import java.io.IOException;

import static com.github.dreamhead.moco.model.MessageContent.content;
import static com.github.dreamhead.moco.util.StringUtil.strip;

public class MessageContentDeserializer extends JsonDeserializer<MessageContent> {
    @Override
    public MessageContent deserialize(final JsonParser jp, final DeserializationContext ctx) throws IOException {
        return content(strip(jp.getText()));
    }
}
