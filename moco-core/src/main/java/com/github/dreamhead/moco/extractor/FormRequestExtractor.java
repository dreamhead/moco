package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.HttpRequestExtractor;
import com.google.common.collect.ImmutableMap;

import java.util.Optional;

import static java.util.Optional.empty;

public final class FormRequestExtractor extends HttpRequestExtractor<String> {
    private final FormsRequestExtractor extractor = new FormsRequestExtractor();
    private final String key;

    public FormRequestExtractor(final String key) {
        this.key = key;
    }

    @Override
    protected Optional<String> doExtract(final HttpRequest request) {
        Optional<ImmutableMap<String, String>> forms = extractor.extract(request);
        if (forms.isPresent()) {
            return Optional.ofNullable(forms.get().get(key));
        }

        return empty();
    }
}
