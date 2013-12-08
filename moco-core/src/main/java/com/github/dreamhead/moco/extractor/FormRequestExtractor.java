package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.RequestExtractor;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import io.netty.handler.codec.http.FullHttpRequest;

import static com.google.common.base.Optional.fromNullable;

public class FormRequestExtractor implements RequestExtractor<String> {
    private final FormsRequestExtractor extractor = new FormsRequestExtractor();
    private final String key;

    public FormRequestExtractor(final String key) {
        this.key = key;
    }

    @Override
    public Optional<String> extract(FullHttpRequest request) {
        Optional<ImmutableMap<String,String>> forms = extractor.extract(request);
        return forms.isPresent() ? fromNullable(forms.get().get(key)) : Optional.<String>absent();
    }
}
