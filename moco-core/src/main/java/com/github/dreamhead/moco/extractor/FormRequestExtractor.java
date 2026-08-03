package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.HttpRequestExtractor;

import java.util.Map;
import java.util.Optional;

public final class FormRequestExtractor extends HttpRequestExtractor<String> {
    private final FormsRequestExtractor extractor = new FormsRequestExtractor();
    private final String key;

    public FormRequestExtractor(final String key) {
        this.key = key;
    }

    @Override
    protected Optional<String> doExtract(final HttpRequest request) {
        Optional<Map<String, String>> forms = extractor.extract(request);
        return forms.map(formValues -> formValues.get(key));
    }
}
