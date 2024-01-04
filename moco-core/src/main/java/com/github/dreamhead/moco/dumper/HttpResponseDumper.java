package com.github.dreamhead.moco.dumper;

import com.github.dreamhead.moco.HttpResponse;
import com.github.dreamhead.moco.Response;
import com.google.common.base.Joiner;
import io.netty.util.internal.StringUtil;

import static com.github.dreamhead.moco.dumper.HttpDumpers.asContent;
import static com.github.dreamhead.moco.dumper.HttpDumpers.asHeaders;

public final class HttpResponseDumper implements Dumper<Response> {
    @Override
    public String dump(final Response response) {
        return responseProtocolLine((HttpResponse) response) +
                StringUtil.NEWLINE +
                asHeaders((HttpResponse) response) +
                asContent((HttpResponse) response);
    }

    private String responseProtocolLine(final HttpResponse httpResponse) {
        return Joiner.on(' ').join(httpResponse.getVersion().text(), httpResponse.getStatus());
    }
}
