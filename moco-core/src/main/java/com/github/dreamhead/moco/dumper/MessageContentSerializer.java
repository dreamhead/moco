package com.github.dreamhead.moco.dumper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.github.dreamhead.moco.model.MessageContent;

import java.io.IOException;

public class MessageContentSerializer extends JsonSerializer<MessageContent> {
    @Override
    public void serialize(MessageContent value, JsonGenerator generator, SerializerProvider serializers) throws IOException {
        generator.writeString(value.toString());
    }
}
