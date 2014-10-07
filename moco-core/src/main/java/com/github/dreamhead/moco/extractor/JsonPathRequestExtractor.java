package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.HttpRequestExtractor;
import com.google.common.base.Optional;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

import java.util.List;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;

public class JsonPathRequestExtractor extends HttpRequestExtractor<String[]> {
	private final ContentRequestExtractor extractor = new ContentRequestExtractor();
	private final JsonPath jsonPath;

	public JsonPathRequestExtractor(final String jsonPath) {
		this.jsonPath = JsonPath.compile(jsonPath);
	}

    @Override
    protected Optional<String[]> doExtract(final HttpRequest request) {
        String content = extractor.extract(request).get();
        try {
            Object jsonPathContent = jsonPath.read(content);
            if (jsonPathContent == null) {
                return absent();
            }
            return of(toStringArray(jsonPathContent));
        } catch (PathNotFoundException e) {
            return absent();
        }
    }

	private String[] toStringArray(Object content){
		if(content instanceof List){
            @SuppressWarnings("unchecked")
            List<String> texts = (List<String>) content;
            return texts.toArray(new String[texts.size()]);
		}

		return new String[]{content.toString()};
	}
}
