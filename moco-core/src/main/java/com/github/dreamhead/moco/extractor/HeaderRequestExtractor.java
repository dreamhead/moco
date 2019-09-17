package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.HttpRequestExtractor;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;

import java.util.Map;
import java.util.Optional;

import static com.github.dreamhead.moco.util.HttpHeaders.isForHeaderName;
import static com.google.common.collect.FluentIterable.from;
import static java.util.Optional.empty;
import static java.util.Optional.of;

public final class HeaderRequestExtractor extends HttpRequestExtractor<String[]> {
    private final String name;

    public HeaderRequestExtractor(final String name) {
        this.name = name;
    }

    @Override
    protected Optional<String[]> doExtract(final HttpRequest request) {
        String[] extractedValues = from(request.getHeaders().entrySet())
                .filter(isForHeaderName(name))
                .transform(toValue())
                .transformAndConcat(arrayAsIterable())
                .toArray(String.class);

        if (extractedValues.length > 0) {
            return of(extractedValues);
        }

        return empty();
    }

    private Function<String[], Iterable<String>> arrayAsIterable() {
        return new Function<String[], Iterable<String>>() {
            @Override
            public Iterable<String> apply(final String[] input) {
                return ImmutableList.copyOf(input);
            }
        };
    }

    private Function<Map.Entry<String, String[]>, String[]> toValue() {
        return new Function<Map.Entry<String, String[]>, String[]>() {
            @Override
            public String[] apply(final Map.Entry<String, String[]> input) {
                return input.getValue();
            }
        };
    }
}
