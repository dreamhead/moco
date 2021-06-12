package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.HttpRequestExtractor;
import com.github.dreamhead.moco.model.MessageContent;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;

public final class JsonPathRequestExtractor extends HttpRequestExtractor<Object> {
    private final ContentRequestExtractor extractor = new ContentRequestExtractor();
    private final JsonPath jsonPath;

    public JsonPathRequestExtractor(final String jsonPath) {
        this.jsonPath = JsonPath.compile(jsonPath);
    }

    @Override
    protected Optional<Object> doExtract(final HttpRequest request) {
        Optional<MessageContent> requestBody = extractor.extract(request);
        return requestBody.flatMap(this::extractContent);
    }

    private Optional<Object> extractContent(final MessageContent content) {
        try {
            Object jsonPathContent = jsonPath.read(content.toInputStream(),
                    content.getCharset().toString(),
                    Configuration.defaultConfiguration());
            if (jsonPathContent == null) {
                return empty();
            }

            return of(toStringArray(jsonPathContent));
        } catch (PathNotFoundException | IOException e) {
            return empty();
        }
    }

    @SuppressWarnings("unchecked")
    private Object toStringArray(final Object content) {
        if (content instanceof List) {
            List list = (List) content;
            return list.stream()
                    .map(Object::toString)
                    .toArray(String[]::new);
        }

        return content.toString();
    }
}
