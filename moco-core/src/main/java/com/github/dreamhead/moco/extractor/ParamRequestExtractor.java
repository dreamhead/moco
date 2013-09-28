package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.RequestExtractor;
import com.google.common.base.Optional;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.List;

import static com.google.common.base.Optional.of;

public class ParamRequestExtractor implements RequestExtractor<String> {
    private final String param;

    public ParamRequestExtractor(String param) {
        this.param = param;
    }

    @Override
    public Optional<String> extract(FullHttpRequest request) {
        QueryStringDecoder decoder = new QueryStringDecoder(request.getUri());
        List<String> values = decoder.parameters().get(param);
        return values == null ? Optional.<String>absent() : of(values.get(0));
    }
}
