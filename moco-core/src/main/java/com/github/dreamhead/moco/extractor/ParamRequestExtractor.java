package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.RequestExtractor;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

import static com.google.common.base.Optional.fromNullable;

public class ParamRequestExtractor implements RequestExtractor<String> {
    private final ParamsRequestExtractor extractor = new ParamsRequestExtractor();
    private final String param;

    public ParamRequestExtractor(final String param) {
        this.param = param;
    }

    @Override
    public Optional<String> extract(HttpRequest request) {
        Optional<ImmutableMap<String,String>> params = extractor.extract(request);
        return params.isPresent() ? fromNullable(params.get().get(this.param)) : Optional.<String>absent();

    }
}
