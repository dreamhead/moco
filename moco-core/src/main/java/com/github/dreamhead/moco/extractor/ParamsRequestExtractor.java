package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.RequestExtractor;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Optional.of;
import static com.google.common.collect.ImmutableMap.copyOf;
import static com.google.common.collect.Maps.newHashMap;

public class ParamsRequestExtractor implements RequestExtractor<ImmutableMap<String, String>> {
    @Override
    public Optional<ImmutableMap<String, String>> extract(HttpRequest request) {
        return of(doExtract(request));
    }

    private ImmutableMap<String, String> doExtract(HttpRequest request) {
        QueryStringDecoder decoder = new QueryStringDecoder(request.getUri());
        Map<String, String> queries = newHashMap();
        for (Map.Entry<String, List<String>> entry : decoder.parameters().entrySet()) {
            queries.put(entry.getKey(), entry.getValue().get(0));
        }

        return copyOf(queries);
    }
}
