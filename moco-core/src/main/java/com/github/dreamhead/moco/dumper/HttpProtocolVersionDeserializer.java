package com.github.dreamhead.moco.dumper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.github.dreamhead.moco.HttpProtocolVersion;

import java.io.IOException;

public class HttpProtocolVersionDeserializer extends JsonDeserializer<HttpProtocolVersion> {
    @Override
    public HttpProtocolVersion deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        return HttpProtocolVersion.versionOf(jp.getText());
    }
}
