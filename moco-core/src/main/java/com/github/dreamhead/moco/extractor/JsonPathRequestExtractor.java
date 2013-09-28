package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.RequestExtractor;
import com.google.common.base.Optional;
import com.jayway.jsonpath.JsonPath;
import io.netty.handler.codec.http.FullHttpRequest;

import java.util.List;

import static com.google.common.base.Optional.of;

public class JsonPathRequestExtractor implements RequestExtractor<String[]> {
	private final ContentRequestExtractor extractor = new ContentRequestExtractor();
	private final JsonPath jsonPath;

	public JsonPathRequestExtractor(String jsonPath) {
		this.jsonPath = JsonPath.compile(jsonPath);
	}

	@Override
	public Optional<String[]> extract(FullHttpRequest request) {
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
