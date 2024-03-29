package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.RequestExtractor;
import com.github.dreamhead.moco.model.MessageContent;

import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;

public final class ContentRequestExtractor implements RequestExtractor<MessageContent> {
    @Override
    public Optional<MessageContent> extract(final Request request) {
        MessageContent content = request.getContent();
        if (content.hasContent()) {
            return of(content);
        }

        return empty();
    }
}
