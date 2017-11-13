package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.RequestExtractor;
import com.google.common.collect.ImmutableMap;

import static java.lang.String.format;

public final class Extractors {
    private static ImmutableMap<String, RequestExtractor<?>> extractors = ImmutableMap.<String, RequestExtractor<?>>builder()
            .put("file", new ContentRequestExtractor())
            .put("text", new ContentRequestExtractor())
            .put("pathresource", new ContentRequestExtractor())
            .put("json", new ContentRequestExtractor())
            .put("uri", new UriRequestExtractor())
            .put("method", new HttpMethodExtractor())
            .put("version", new VersionExtractor()).build();

    public static RequestExtractor<?> extractor(final String id) {
        if (extractors.containsKey(id)) {
            return extractors.get(id);
        }

        throw new IllegalArgumentException(format("unknown extractor for [%s]", id));
    }

    private Extractors() {
    }
}
