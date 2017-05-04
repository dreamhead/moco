package com.github.dreamhead.moco.dumper;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.github.dreamhead.moco.model.MessageContent;

import java.io.IOException;
import java.nio.charset.Charset;

public class MessageContentSerializer extends JsonSerializer<MessageContent> {
    @Override
    public void serialize(final MessageContent value, final JsonGenerator generator,
                          final SerializerProvider serializers) throws IOException {
        if (value.hasCharset()) {
            generator.writeObject(new InternalMessageContent(value.getContent(), value.getCharset()));
            return;
        }

        generator.writeString(new String(value.getContent()));
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static class InternalMessageContent {
        private byte[] content;
        private Charset charset;

        public InternalMessageContent(byte[] content, Charset charset) {
            this.content = content;
            this.charset = charset;
        }
    }
}
