package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.RequestExtractor;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.List;

public class ParamRequestExtractor implements RequestExtractor<String> {
    private final String param;

    public ParamRequestExtractor(String param) {
        this.param = param;
    }

    @Override
    public String extract(HttpRequest request) {
        QueryStringDecoder decoder = new QueryStringDecoder(request.getUri());
        List<String> values = decoder.parameters().get(param);
        return values == null ? null : values.get(0);
    }
}
