package com.github.dreamhead.moco.parser.model;

import com.github.dreamhead.moco.HttpHeader;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

import java.util.Map;
import java.util.Set;

import static com.github.dreamhead.moco.Moco.header;

public abstract class BaseActionSetting {
    protected HttpHeader[] asHeaders(Set<Map.Entry<String, TextContainer>> iterable) {
        return FluentIterable.from(iterable)
                .transform(asHeader())
                .toArray(HttpHeader.class);
    }

    private Function<Map.Entry<String, TextContainer>, HttpHeader> asHeader() {
        return new Function<Map.Entry<String, TextContainer>, HttpHeader>() {
            @Override
            public HttpHeader apply(Map.Entry<String, TextContainer> input) {
                TextContainer value = input.getValue();
                return header(input.getKey(), value.asResource());
            }
        };
    }
}
