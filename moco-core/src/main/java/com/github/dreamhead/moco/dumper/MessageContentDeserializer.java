package com.github.dreamhead.moco.dumper;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.github.dreamhead.moco.model.MessageContent;

import java.io.IOException;
import java.nio.charset.Charset;

import static com.github.dreamhead.moco.model.MessageContent.content;
import static com.github.dreamhead.moco.util.StringUtil.strip;

public class MessageContentDeserializer extends JsonDeserializer<MessageContent> {
    @Override
    public MessageContent deserialize(final JsonParser jp, final DeserializationContext ctx) throws IOException {
        JsonToken currentToken = jp.getCurrentToken();
        if (currentToken == JsonToken.VALUE_STRING) {
            return content(strip(jp.getText()));
        }

        if (currentToken == JsonToken.START_OBJECT) {
            InternalMessageContent content = jp.readValueAs(InternalMessageContent.class);
            return content.toContent();
        }

        return (MessageContent) ctx.handleUnexpectedToken(MessageContent.class, jp);
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static class InternalMessageContent {
        private byte[] content;
        private Charset charset;

        public MessageContent toContent() {
            return content().withCharset(charset).withContent(content).build();
        }
    }
}
