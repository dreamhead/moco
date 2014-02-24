package com.github.dreamhead.moco.dumper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.github.dreamhead.moco.HttpProtocolVersion;

import java.io.IOException;

public class HttpProtocolVersionSerializer extends JsonSerializer<HttpProtocolVersion> {
    @Override
    public void serialize(HttpProtocolVersion value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeString(value.text());
    }
}
