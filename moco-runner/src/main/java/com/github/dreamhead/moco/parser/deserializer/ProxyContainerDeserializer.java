package com.github.dreamhead.moco.parser.deserializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.github.dreamhead.moco.parser.model.FailoverContainer;
import com.github.dreamhead.moco.parser.model.ProxyContainer;
import com.github.dreamhead.moco.parser.model.TextContainer;

import java.io.IOException;

import static com.github.dreamhead.moco.parser.model.ProxyContainer.builder;
import static com.github.dreamhead.moco.util.Strings.strip;

public final class ProxyContainerDeserializer extends JsonDeserializer<ProxyContainer> {
    @Override
    public ProxyContainer deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        JsonToken currentToken = jp.getCurrentToken();
        if (currentToken == JsonToken.VALUE_STRING) {
            return builder().withUrl(strip(jp.getText())).build();
        }

        if (currentToken == JsonToken.START_OBJECT) {
            InternalProxyContainer container = jp.readValueAs(InternalProxyContainer.class);
            return container.toProxyContainer();
        }

        return (ProxyContainer) ctxt.handleUnexpectedToken(ProxyContainer.class, jp);
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static class InternalProxyContainer {
        private TextContainer url;
        private String from;
        private String to;

        private FailoverContainer failover;
        private FailoverContainer playback;

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
