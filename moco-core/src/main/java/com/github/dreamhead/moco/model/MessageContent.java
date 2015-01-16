package com.github.dreamhead.moco.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.dreamhead.moco.dumper.MessageContentDeserializer;
import com.github.dreamhead.moco.dumper.MessageContentSerializer;
import com.google.common.base.Objects;
import com.google.common.base.Optional;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

@JsonSerialize(using = MessageContentSerializer.class)
@JsonDeserialize(using = MessageContentDeserializer.class)
public class MessageContent {
    private byte[] content;
    private Optional<Charset> charset;

    public byte[] getContent() {
        return content;
    }

    public Optional<Charset> getCharset() {
        return charset;
    }

    public boolean hasContent() {
        return content.length > 0;
    }

    @Override
    public String toString() {
        return new String(content, charset.or(Charset.defaultCharset()));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof MessageContent)) {
            return false;
        }

        MessageContent that = (MessageContent) obj;
        return Objects.equal(this.charset, that.charset) && Arrays.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.content, this.charset);
    }

    public static Builder content() {
        return new Builder();
    }

    public InputStream toInputStream() {
        return new ByteArrayInputStream(this.content);
    }

    public static class Builder {
        private byte[] content;
        private Charset charset;

        public Builder withContent(String content) {
            this.content = content.getBytes();
            return this;
        }

        public Builder withContent(byte[] content) {
            this.content = content;
            return this;
        }

        public Builder withCharset(Charset charset) {
            this.charset = charset;
            return this;
        }

        public MessageContent build() {
            MessageContent messageContent = new MessageContent();
            messageContent.charset = Optional.fromNullable(charset);
            messageContent.content = (content == null ? new byte[0] : content);
            return messageContent;
        }
    }
}
