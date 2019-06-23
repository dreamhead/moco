package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.dreamhead.moco.HttpHeader;
import com.github.dreamhead.moco.MocoEventAction;
import com.github.dreamhead.moco.util.Iterables;
import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.collect.FluentIterable;

import java.util.Map;

import static com.github.dreamhead.moco.Moco.get;
import static com.github.dreamhead.moco.Moco.header;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public final class GetSetting {
    private TextContainer url;
    private Map<String, TextContainer> headers;

    public MocoEventAction createAction() {
        if (headers == null) {
            return get(url.asResource());
        }

        HttpHeader[] headers = FluentIterable.from(this.headers.entrySet())
                .transform(asHeader())
                .toArray(HttpHeader.class);

        return get(url.asResource(), Iterables.head(headers), Iterables.tail(headers));
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

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("url", url)
                .add("headers", this.headers)
                .toString();
    }
}
