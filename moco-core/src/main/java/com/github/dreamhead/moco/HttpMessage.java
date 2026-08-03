package com.github.dreamhead.moco;

import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.util.HttpHeaders;

import java.util.Map;

public interface HttpMessage extends Message {
    HttpProtocolVersion getVersion();

    Map<String, String[]> getHeaders();

    String getHeader(String name);

    default boolean hasContent() {
        String lengthText = this.getHeader(HttpHeaders.CONTENT_LENGTH);
        if (lengthText != null) {
            return true;
        }

        MessageContent content = this.getContent();
        return content != null && content.hasContent();
    }
}
