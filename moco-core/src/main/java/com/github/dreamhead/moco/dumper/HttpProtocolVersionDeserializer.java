package com.github.dreamhead.moco.dumper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.github.dreamhead.moco.HttpProtocolVersion;

import java.io.IOException;

public class HttpProtocolVersionDeserializer extends JsonDeserializer<HttpProtocolVersion> {
    @Override
    public HttpProtocolVersion deserialize(final JsonParser jp, final DeserializationContext ctx) throws IOException {
        try {
            return HttpProtocolVersion.versionOf(jp.getText());
        } catch (IllegalArgumentException e) {
            return (HttpProtocolVersion) ctx.handleUnexpectedToken(HttpProtocolVersion.class, jp);
        }
    }
}
