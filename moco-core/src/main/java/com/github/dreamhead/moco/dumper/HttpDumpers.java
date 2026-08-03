package com.github.dreamhead.moco.dumper;

import com.github.dreamhead.moco.HttpMessage;
import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.util.HttpHeaders;
import com.github.dreamhead.moco.util.MediaType;
import io.netty.util.internal.StringUtil;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class HttpDumpers {
    public static String asContent(final HttpMessage message) {
        if (message.hasContent()) {
            String content = contentForDump(message);
            if (content.isEmpty()) {
                return "";
            }
            return StringUtil.NEWLINE + StringUtil.NEWLINE + content;
        }

        return "";
    }

    private static String contentForDump(final HttpMessage message) {
        String type = message.getHeader(HttpHeaders.CONTENT_TYPE);
        if (isText(type)) {
            return message.getContent().toString();
        }

        MessageContent content = message.getContent();
        if (content == null || !content.hasContent()) {
            return "";
        }

        return "<content is binary>";
    }

    private static boolean isText(final String type) {
        try {
            MediaType mediaType = MediaType.parse(type);
            return mediaType.type().equals("text")
                    || mediaType.subtype().endsWith("javascript")
                    || mediaType.subtype().endsWith("json")
                    || mediaType.subtype().endsWith("xml")
                    || isSameType(mediaType, MediaType.FORM_DATA)
                    || mediaType.subtype().endsWith("form-data");
        } catch (Exception e) {
            return false;
        }
    }

    // Guava's is() also handled wildcards and parameter subsets; FORM_DATA carries neither,
    // so the comparison it performed here reduces to type and subtype equality.
    private static boolean isSameType(final MediaType actual, final MediaType expected) {
        return actual.type().equals(expected.type()) && actual.subtype().equals(expected.subtype());
    }

    public static String asHeaders(final HttpMessage message) {
        return message.getHeaders().entrySet().stream()
                .flatMap(HttpDumpers::toHeaderLines)
                .collect(Collectors.joining(StringUtil.NEWLINE));
    }

    private static Stream<String> toHeaderLines(final Map.Entry<String, String[]> input) {
        String key = input.getKey();
        return Arrays.stream(input.getValue())
                .map(value -> key + ": " + value);
    }

    private HttpDumpers() {
    }
}
