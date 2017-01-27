package com.github.dreamhead.moco.parser.deserializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.github.dreamhead.moco.parser.model.ProxyContainer;

import java.io.IOException;

import static com.github.dreamhead.moco.parser.model.ProxyContainer.builder;
import static com.google.common.collect.Iterators.get;

public class ProxyContainerDeserializer extends JsonDeserializer<ProxyContainer> {
    @Override
    public ProxyContainer deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        JsonToken currentToken = jp.getCurrentToken();
        if (currentToken == JsonToken.VALUE_STRING) {
            return builder().withUrl(jp.getText().trim()).build();
        }

        if (currentToken == JsonToken.START_OBJECT) {
            InternalProxyContainer container = get(jp.readValuesAs(InternalProxyContainer.class), 0);
            return container.toProxyContainer();
        }

        return (ProxyContainer) ctxt.handleUnexpectedToken(ProxyContainer.class, jp);
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static class InternalProxyContainer {
        private String url;
        private String from;
        private String to;

        private String failover;
        private String playback;

        public ProxyContainer toProxyContainer() {
            return builder()
                    .withUrl(url)
                    .withFrom(from)
                    .withTo(to)
                    .withFailover(failover)
                    .withPlayback(playback)
                    .build();
        }
    }
}
