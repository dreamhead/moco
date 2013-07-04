package com.github.dreamhead.moco.extractor;

import java.util.List;

import static com.google.common.collect.FluentIterable.*;
import org.jboss.netty.handler.codec.http.HttpRequest;

import com.github.dreamhead.moco.RequestExtractor;
import com.jayway.jsonpath.JsonPath;

public class JsonPathRequestExtractor implements RequestExtractor<String[]> {
	
	private final ContentRequestExtractor extractor = new ContentRequestExtractor();
	private final JsonPath jsonPath;

	public JsonPathRequestExtractor(String jsonPath) {
		this.jsonPath = JsonPath.compile(jsonPath);
	}

	@Override
	public String[] extract(HttpRequest request) {
		return toStringArray(jsonPath.read(extractor.extract(request)));
	}
	
	private String[] toStringArray(Object actualValue){
		String[] valueArray = null;
		if(actualValue instanceof List){
			valueArray = from((List<String>)actualValue).toArray(String.class);
		}else{
			valueArray = new String[]{(String)actualValue};
		}
		return valueArray;
	}

}
