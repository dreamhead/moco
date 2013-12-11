package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.RequestExtractor;
import com.google.common.base.Optional;
import com.jayway.jsonpath.JsonPath;

import java.util.List;

import static com.google.common.base.Optional.of;

public class JsonPathRequestExtractor implements RequestExtractor<String[]> {
	private final ContentRequestExtractor extractor = new ContentRequestExtractor();
	private final JsonPath jsonPath;

	public JsonPathRequestExtractor(final String jsonPath) {
		this.jsonPath = JsonPath.compile(jsonPath);
	}

	@Override
	public Optional<String[]> extract(HttpRequest request) {
		return of(toStringArray(jsonPath.read(extractor.extract(request).get())));
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
