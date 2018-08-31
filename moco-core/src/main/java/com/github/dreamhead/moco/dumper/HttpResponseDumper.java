package com.github.dreamhead.moco.dumper;

import com.github.dreamhead.moco.HttpResponse;
import com.github.dreamhead.moco.Response;
import io.netty.util.internal.StringUtil;

import static com.github.dreamhead.moco.dumper.HttpDumpers.asContent;
import static com.github.dreamhead.moco.dumper.HttpDumpers.asHeaders;

public final class HttpResponseDumper implements Dumper<Response> {
    @Override
    public String dump(final Response response) {
        HttpResponse httpResponse = (HttpResponse) response;
        return new StringBuilder()
                .append(responseProtocolLine(httpResponse))
                .append(StringUtil.NEWLINE)
                .append(asHeaders(httpResponse))
                .append(asContent(httpResponse))
                .toString();
    }

    private String responseProtocolLine(final HttpResponse httpResponse) {
        return httpResponse.getVersion().text() + ' ' + httpResponse.getStatus();
    }
}
