package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.RequestExtractor;
import com.google.common.base.Optional;

import static com.google.common.base.Optional.of;
import static com.google.common.base.Strings.isNullOrEmpty;

public class ContentRequestExtractor implements RequestExtractor<String> {
    @Override
    public Optional<String> extract(HttpRequest request) {
        String content = request.getContent();
        return isNullOrEmpty(content) ? Optional.<String>absent() : of(content);
    }
}
