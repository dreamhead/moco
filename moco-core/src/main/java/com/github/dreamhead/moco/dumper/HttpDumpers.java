package com.github.dreamhead.moco.dumper;

import com.github.dreamhead.moco.HttpMessage;
import io.netty.util.internal.StringUtil;

public class HttpDumpers {
    public static void appendContent(HttpMessage message, StringBuilder buf) {
        long length = getContentLength(message, -1);
        if (length > 0) {
            buf.append(StringUtil.NEWLINE);
            buf.append(StringUtil.NEWLINE);
            String type = message.getHeaders().get("Content-Type");
            if (isText(type)) {
                buf.append(message.getContent());
            } else {
                buf.append("<content is binary>");
            }
        }
    }

    private static boolean isText(String type) {
        return type == null || type.startsWith("text") || type.endsWith("javascript");
    }

    private static long getContentLength(HttpMessage response, long defaultValue) {
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

    private HttpDumpers() {}
}
