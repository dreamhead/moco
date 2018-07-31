package com.github.dreamhead.moco;

import com.github.dreamhead.moco.model.MessageContent;

public interface MutableResponse extends Response {
    void setContent(MessageContent content);
}
