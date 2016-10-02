package com.github.dreamhead.moco.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.dreamhead.moco.MocoException;
import com.github.dreamhead.moco.dumper.MessageContentDeserializer;
import com.github.dreamhead.moco.dumper.MessageContentSerializer;
import com.google.common.base.Objects;
import com.google.common.base.Optional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

import static com.google.common.io.ByteStreams.toByteArray;

@JsonSerialize(using = MessageContentSerializer.class)
@JsonDeserialize(using = MessageContentDeserializer.class)
public class MessageContent {
    private byte[] content;
    private Optional<Charset> charset;

    public byte[] getContent() {
        return content;
    }

    public Charset getCharset() {
        return charset.or(Charset.defaultCharset());
    }

    public boolean hasContent() {
        return content.length > 0;
    }

    @Override
    public String toString() {
        return new String(content, getCharset());
    }

    @Override
    public boolean equals(final Object obj) {
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
        return Objects.hashCode(Arrays.hashCode(this.content), this.charset);
    }

    public static Builder content() {
        return new Builder();
    }

    public static MessageContent content(final String content) {
        return content().withContent(content).build();
    }

    public InputStream toInputStream() {
        return new ByteArrayInputStream(this.content);
    }

    public static class Builder {
        private byte[] content;
        private Charset charset;

        public Builder withContent(final String content) {
            this.content = content.getBytes();
            return this;
        }

        public Builder withContent(final InputStream is) {
            try {
                this.content = toByteArray(is);
                return this;
            } catch (IOException e) {
                throw new MocoException(e);
            }
        }

        public Builder withContent(final byte[] content) {
            this.content = content;
            return this;
        }

        public Builder withCharset(final Charset charset) {
            this.charset = charset;
            return this;
        }

        public MessageContent build() {
            MessageContent messageContent = new MessageContent();
            messageContent.charset = Optional.fromNullable(charset);
            messageContent.content = targetContent(content);
            return messageContent;
        }

        private byte[] targetContent(final byte[] content) {
            if (content == null) {
                return new byte[0];
            }

            return this.content;
        }
    }
}
