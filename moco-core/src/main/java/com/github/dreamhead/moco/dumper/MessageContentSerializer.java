package com.github.dreamhead.moco.dumper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.github.dreamhead.moco.model.MessageContent;

import java.io.IOException;

public class MessageContentSerializer extends JsonSerializer<MessageContent> {
    @Override
    public final void serialize(final MessageContent value, final JsonGenerator generator,
                          final SerializerProvider serializers) throws IOException {
        if (value.hasCharset()) {
            generator.writeObject(new InternalMessageContent(value.getContent(), value.getCharset()));
            return;
        }

        generator.writeString(new String(value.getContent()));
    }
}
