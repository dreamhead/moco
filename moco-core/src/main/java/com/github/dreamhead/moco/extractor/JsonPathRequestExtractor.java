package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.HttpRequestExtractor;
import com.github.dreamhead.moco.model.MessageContent;
import com.google.common.base.Optional;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;

public final class JsonPathRequestExtractor extends HttpRequestExtractor<Object> {
    private final ContentRequestExtractor extractor = new ContentRequestExtractor();
    private final JsonPath jsonPath;

    public JsonPathRequestExtractor(final String jsonPath) {
        this.jsonPath = JsonPath.compile(jsonPath);
    }

    @Override
    protected Optional<Object> doExtract(final HttpRequest request) {
        Optional<MessageContent> requestBody = extractor.extract(request);
        try {
            if (!requestBody.isPresent()) {
                return absent();
            }

            MessageContent content = requestBody.get();
            Object jsonPathContent = jsonPath.read(new ByteArrayInputStream(content.getContent()),
                    content.getCharset().toString(),
                    Configuration.defaultConfiguration());
            if (jsonPathContent == null) {
                return absent();
            }
            return of(toStringArray(jsonPathContent));
        } catch (PathNotFoundException | IOException e) {
            return absent();
        }
    }

    private Object toStringArray(final Object content) {
        if (content instanceof List) {
            @SuppressWarnings("unchecked")
            List<String> texts = (List<String>) content;
            return texts.toArray(new String[texts.size()]);
        }

        return content.toString();
    }
}
