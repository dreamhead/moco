package com.github.dreamhead.moco.dumper;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.dreamhead.moco.model.MessageContent;

import java.nio.charset.Charset;

import static com.github.dreamhead.moco.model.MessageContent.content;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public final class InternalMessageContent {
    private byte[] content;
    private Charset charset;

    public InternalMessageContent(@JsonProperty("content") final byte[] content,
                                  @JsonProperty("charset") final Charset charset) {
        this.content = content;
        this.charset = charset;
    }

    public MessageContent toContent() {
        return content().withCharset(charset).withContent(content).build();
    }
}
