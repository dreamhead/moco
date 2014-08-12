package com.github.dreamhead.moco.dumper;

import com.github.dreamhead.moco.HttpResponse;
import com.github.dreamhead.moco.Response;
import com.google.common.base.Joiner;
import io.netty.util.internal.StringUtil;

import static com.github.dreamhead.moco.dumper.HttpDumpers.appendContent;

public class HttpResponseDumper implements Dumper<Response> {
    private final Joiner.MapJoiner headerJoiner = Joiner.on(StringUtil.NEWLINE).withKeyValueSeparator(": ");

    @Override
    public String dump(Response response) {
        HttpResponse httpResponse = (HttpResponse)response;
        StringBuilder buf = new StringBuilder();
        appendResponseProtocolLine(httpResponse, buf);
        buf.append(StringUtil.NEWLINE);
        headerJoiner.appendTo(buf, httpResponse.getHeaders());
        appendContent(httpResponse, buf);

        return buf.toString();
    }

    private void appendResponseProtocolLine(HttpResponse response, StringBuilder buf) {
        buf.append(response.getVersion().text());
        buf.append(' ');
        buf.append(response.getStatus());
    }
}
