package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.RequestExtractor;

import java.util.Map;

public final class Extractors {
    private static Map<String, RequestExtractor<?>> extractors = Map.of(
            "file", new ContentRequestExtractor(),
            "text", new ContentRequestExtractor(),
            "pathresource", new ContentRequestExtractor(),
            "binary", new ContentRequestExtractor(),
            "xml", new ContentRequestExtractor(),
            "json", new ContentRequestExtractor(),
            "uri", new UriRequestExtractor(),
            "method", new HttpMethodExtractor(),
            "version", new VersionExtractor());

    public static RequestExtractor<?> extractor(final String id) {
        if (extractors.containsKey(id)) {
            return extractors.get(id);
        }

        throw new IllegalArgumentException("unknown extractor for [%s]".formatted(id));
    }

    private Extractors() {
    }
}
