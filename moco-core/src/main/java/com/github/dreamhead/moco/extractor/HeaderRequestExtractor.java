package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.RequestExtractor;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;

public class HeaderRequestExtractor implements RequestExtractor<String> {
    private final String name;

    public HeaderRequestExtractor(final String name) {
        this.name = name;
    }

    @Override
    public Optional<String> extract(final HttpRequest request) {
        ImmutableMap<String,String> headers = request.getHeaders();
        for (String key : headers.keySet()) {
            if (key.equalsIgnoreCase(name)) {
                return of(headers.get(key));
            }
        }

        return absent();
    }
}
