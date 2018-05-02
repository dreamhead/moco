package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.RequestExtractor;
import com.github.dreamhead.moco.model.MessageContent;
import com.google.common.base.Optional;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;

public final class ContentRequestExtractor implements RequestExtractor<MessageContent> {
    @Override
    public Optional<MessageContent> extract(final Request request) {
        MessageContent content = request.getContent();
        if (content.hasContent() || content.getContent().length > 0) {
            return of(content);
        }

        return absent();
    }
}
