package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.HttpRequestExtractor;
import com.github.dreamhead.moco.util.HttpHeaders;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;

public final class HeaderRequestExtractor extends HttpRequestExtractor<String[]> {
    private final String name;

    public HeaderRequestExtractor(final String name) {
        this.name = name;
    }

    @Override
    protected Optional<String[]> doExtract(final HttpRequest request) {
        String[] extractedValues = request.getHeaders().entrySet().stream()
                .filter(input -> HttpHeaders.isSameHeaderName(input.getKey(), name))
                .map(Map.Entry::getValue)
                .flatMap(Arrays::stream)
                .toArray(String[]::new);

        if (extractedValues.length > 0) {
            return of(extractedValues);
        }

        return empty();
    }

}
