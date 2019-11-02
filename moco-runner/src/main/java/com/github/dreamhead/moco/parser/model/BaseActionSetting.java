package com.github.dreamhead.moco.parser.model;

import com.github.dreamhead.moco.HttpHeader;

import java.util.Map;

import static com.github.dreamhead.moco.Moco.header;

public abstract class BaseActionSetting {
    protected final HttpHeader[] asHeaders(final Map<String, TextContainer> headers) {
        if (headers == null) {
            return new HttpHeader[0];
        }

        return headers.entrySet().stream()
                .map(input -> header(input.getKey(), input.getValue().asResource()))
                .toArray(HttpHeader[]::new);
    }
}
