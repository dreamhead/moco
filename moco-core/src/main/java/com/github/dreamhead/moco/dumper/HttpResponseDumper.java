package com.github.dreamhead.moco.dumper;

import com.github.dreamhead.moco.HttpResponse;
import com.github.dreamhead.moco.Response;
import com.google.common.base.Joiner;
import io.netty.util.internal.StringUtil;

import static com.github.dreamhead.moco.dumper.HttpDumpers.asContent;

public class HttpResponseDumper implements Dumper<Response> {
    private final Joiner.MapJoiner headerJoiner = Joiner.on(StringUtil.NEWLINE).withKeyValueSeparator(": ");

    @Override
    public String dump(final Response response) {
        HttpResponse httpResponse = (HttpResponse) response;
        StringBuilder buf = new StringBuilder();
        buf.append(responseProtocolLine(httpResponse))
                .append(StringUtil.NEWLINE)
                .append(headerJoiner.join(httpResponse.getHeaders()))
                .append(asContent(httpResponse));

        return buf.toString();
    }

    private String responseProtocolLine(final HttpResponse httpResponse) {
        return httpResponse.getVersion().text() + ' ' + httpResponse.getStatus();
    }
}
