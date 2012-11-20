package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.RequestExtractor;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;

import java.util.List;
import java.util.Map;

public class ParamRequestExtractor implements RequestExtractor {
    private String param;

    public ParamRequestExtractor(String param) {
        this.param = param;
    }

    @Override
    public String extract(HttpRequest request) {
        QueryStringDecoder decoder = new QueryStringDecoder(request.getUri());
        List<String> values = decoder.getParameters().get(param);
        return values == null ? null : values.get(0);
    }
}
