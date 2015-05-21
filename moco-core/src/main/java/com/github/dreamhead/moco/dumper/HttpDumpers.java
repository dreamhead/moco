package com.github.dreamhead.moco.dumper;

import com.github.dreamhead.moco.HttpMessage;
import com.google.common.net.HttpHeaders;
import io.netty.util.internal.StringUtil;

public class HttpDumpers {
    public static String asContent(final HttpMessage message) {
        long length = getContentLength(message, -1);
        if (length > 0) {
            return StringUtil.NEWLINE + StringUtil.NEWLINE + contentForDump(message);
        }

        return "";
    }

    private static String contentForDump(final HttpMessage message) {
        String type = message.getHeaders().get(HttpHeaders.CONTENT_TYPE);
        if (isText(type)) {
            return message.getContent().toString();
        }

        return "<content is binary>";
    }

    private static boolean isText(final String type) {
        return type == null || type.startsWith("text") || type.endsWith("javascript") || type.endsWith("json") || type.endsWith("xml");
    }

    private static long getContentLength(final HttpMessage response, final long defaultValue) {
        String lengthText = response.getHeaders().get(HttpHeaders.CONTENT_LENGTH);
        if (lengthText != null) {
            try {
                return Long.parseLong(lengthText);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }

        return defaultValue;
    }

    private HttpDumpers() {}
}
