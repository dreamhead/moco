package com.github.dreamhead.moco.dumper;

import com.github.dreamhead.moco.HttpResponse;
import com.github.dreamhead.moco.Response;
import io.netty.util.internal.StringUtil;

public class HttpResponseDumper extends HttpMessageBaseDumper<Response> {
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

    private static void appendContent(HttpResponse message, StringBuilder buf) {
        long length = getContentLength(message, -1);
        if (length > 0) {
            buf.append(StringUtil.NEWLINE);
            buf.append(StringUtil.NEWLINE);
            buf.append(message.getContent());
        }
    }

    private static long getContentLength(HttpResponse response, long defaultValue) {
        String lengthText = response.getHeaders().get("Content-Length");
        if (lengthText != null) {
            try {
                return Long.parseLong(lengthText);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }

        return defaultValue;
    }
}
