package com.github.dreamhead.moco.dumper;

import com.github.dreamhead.moco.HttpMessage;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import io.netty.util.internal.StringUtil;

public final class HttpDumpers {
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
        try {
            MediaType mediaType = MediaType.parse(type);
            return mediaType.is(MediaType.ANY_TEXT_TYPE)
                    || mediaType.subtype().endsWith("javascript")
                    || mediaType.subtype().endsWith("json")
                    || mediaType.subtype().endsWith("xml")
                    || mediaType.is(MediaType.FORM_DATA);
        } catch (Exception e) {
            return false;
        }
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

    private HttpDumpers() {
    }
}
