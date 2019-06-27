package com.github.dreamhead.moco.parser.model;

import com.github.dreamhead.moco.HttpHeader;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

import java.util.Map;

import static com.github.dreamhead.moco.Moco.header;

public abstract class BaseActionSetting {
    protected HttpHeader[] asHeaders(final Map<String, TextContainer> headers) {
        if (headers == null) {
            return new HttpHeader[0];
        }

        return FluentIterable.from(headers.entrySet())
                .transform(asHeader())
                .toArray(HttpHeader.class);
    }

    private Function<Map.Entry<String, TextContainer>, HttpHeader> asHeader() {
        return new Function<Map.Entry<String, TextContainer>, HttpHeader>() {
            @Override
            public HttpHeader apply(final Map.Entry<String, TextContainer> input) {
                TextContainer value = input.getValue();
                return header(input.getKey(), value.asResource());
            }
        };
    }
}
