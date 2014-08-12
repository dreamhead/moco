package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.HttpRequestExtractor;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

import static com.google.common.base.Optional.fromNullable;

public class FormRequestExtractor extends HttpRequestExtractor<String> {
    private final FormsRequestExtractor extractor = new FormsRequestExtractor();
    private final String key;

    public FormRequestExtractor(final String key) {
        this.key = key;
    }

    @Override
    protected Optional<String> doExtract(final HttpRequest request) {
        Optional<ImmutableMap<String,String>> forms = extractor.extract(request);
        return forms.isPresent() ? fromNullable(forms.get().get(key)) : Optional.<String>absent();
    }
}
