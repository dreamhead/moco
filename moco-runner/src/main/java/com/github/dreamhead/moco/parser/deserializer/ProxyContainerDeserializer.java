package com.github.dreamhead.moco.parser.deserializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import com.github.dreamhead.moco.parser.model.FailoverContainer;
import com.github.dreamhead.moco.parser.model.ProxyContainer;
import com.github.dreamhead.moco.parser.model.TextContainer;

import static com.github.dreamhead.moco.parser.model.ProxyContainer.builder;
import static com.github.dreamhead.moco.util.Strings.strip;

public final class ProxyContainerDeserializer extends ValueDeserializer<ProxyContainer> {
    @Override
    public ProxyContainer deserialize(final JsonParser jp, final DeserializationContext ctxt)  {
        JsonToken currentToken = jp.currentToken();
        if (currentToken == JsonToken.VALUE_STRING) {
            return builder().withUrl(strip(jp.getValueAsString())).build();
        }

        if (currentToken == JsonToken.START_OBJECT) {
            InternalProxyContainer container = jp.readValueAs(InternalProxyContainer.class);
            return container.toProxyContainer();
        }

        return (ProxyContainer) ctxt.handleUnexpectedToken(ProxyContainer.class, jp);
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static final class InternalProxyContainer {
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
