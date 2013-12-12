package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.RequestExtractor;
import com.google.common.base.Optional;

import static com.google.common.base.Optional.fromNullable;

public class ContentRequestExtractor implements RequestExtractor<String> {
    @Override
    public Optional<String> extract(HttpRequest request) {
        return fromNullable(request.getContent());
    }
}
