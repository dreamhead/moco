package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.RequestExtractor;
import com.google.common.base.Splitter;
import org.jboss.netty.handler.codec.http.HttpRequest;

public class ParamRequestExtractor implements RequestExtractor {
    private String param;

    public ParamRequestExtractor(String param) {
        this.param = param;
    }

    @Override
    public String extract(HttpRequest request) {
        String uri = request.getUri();
        int index = uri.indexOf("?");
        return index == -1 ? null : extractFromParameters(uri.substring(index + 1));
    }

    private String extractFromParameters(String parameterPart) {
        Iterable<String> results = Splitter.on("&").omitEmptyStrings().trimResults().split(parameterPart);
        for (String result : results) {
            int parameterIndex = result.indexOf("=");
            String key = result.substring(0, parameterIndex);
            if (param.equals(key)) {
                return result.substring(parameterIndex + 1);
            }
        }

        return null;
    }
}
