package com.github.dreamhead.moco.dumper;

import com.github.dreamhead.moco.HttpMessage;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import io.netty.util.internal.StringUtil;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.collect.ImmutableList.toImmutableList;

public final class HttpDumpers {
    public static String asContent(final HttpMessage message) {
        if (message.hasContent()) {
            return StringUtil.NEWLINE + StringUtil.NEWLINE + contentForDump(message);
        }

        return "";
    }

    private static String contentForDump(final HttpMessage message) {
        String type = message.getHeader(HttpHeaders.CONTENT_TYPE);
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
                    || mediaType.is(MediaType.FORM_DATA)
                    || mediaType.subtype().endsWith("form-data");
        } catch (Exception e) {
            return false;
        }
    }


    private static final Joiner.MapJoiner HEAD_JOINER = Joiner.on(StringUtil.NEWLINE).withKeyValueSeparator(": ");

    public static String asHeaders(final HttpMessage message) {
        return HEAD_JOINER.join(message.getHeaders().entrySet().stream()
                .flatMap(HttpDumpers::toEntries)
                .collect(Collectors.toList()));
    }

    private static Stream<Map.Entry<String, String>> toEntries(final Map.Entry<String, String[]> input) {
        String key = input.getKey();
        return Arrays.stream(input.getValue())
                .map(value -> Maps.immutableEntry(key, value))
                .collect(toImmutableList())
                .stream();
    }

    private HttpDumpers() {
    }
}
