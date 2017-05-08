package com.github.dreamhead.moco.dumper;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.dreamhead.moco.model.MessageContent;

import java.nio.charset.Charset;

import static com.github.dreamhead.moco.model.MessageContent.content;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class InternalMessageContent {
    private byte[] content;
    private Charset charset;

    public InternalMessageContent(byte[] content, Charset charset) {
        this.content = content;
        this.charset = charset;
    }

    public MessageContent toContent() {
        return content().withCharset(charset).withContent(content).build();
    }
}
