package com.github.dreamhead.moco;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.dreamhead.moco.dumper.HttpProtocolVersionDeserializer;
import com.github.dreamhead.moco.dumper.HttpProtocolVersionSerializer;

import java.util.Arrays;

@JsonSerialize(using = HttpProtocolVersionSerializer.class)
@JsonDeserialize(using = HttpProtocolVersionDeserializer.class)
public enum HttpProtocolVersion {
    VERSION_0_9("HTTP/0.9"),
    VERSION_1_0("HTTP/1.0"),
    VERSION_1_1("HTTP/1.1");

    private final String text;

    HttpProtocolVersion(final String text) {
        this.text = text;
    }

    public String text() {
        return text;
    }

    @Override
    public String toString() {
        return text;
    }

    public static HttpProtocolVersion versionOf(final String version) {
        HttpProtocolVersion[] values = HttpProtocolVersion.values();
        return Arrays.stream(values)
                .filter(value -> value.text.equalsIgnoreCase(version))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("unknown HTTP version: " + version));
    }
}
