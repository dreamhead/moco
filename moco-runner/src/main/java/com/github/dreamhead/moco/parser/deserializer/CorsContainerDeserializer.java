package com.github.dreamhead.moco.parser.deserializer;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.github.dreamhead.moco.parser.model.CorsContainer;
import com.github.dreamhead.moco.parser.model.LatencyContainer;

import java.io.IOException;
import java.util.List;

public final class CorsContainerDeserializer extends JsonDeserializer<CorsContainer> {
    @Override
    public CorsContainer deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        JsonToken currentToken = jp.getCurrentToken();
        if (currentToken == JsonToken.VALUE_TRUE) {
            return CorsContainer.newContainer();
        }

        if (currentToken == JsonToken.START_OBJECT) {
            jp.nextToken();
            CorsContainerDeserializer.InternalCorsContainer container = jp.readValueAs(CorsContainerDeserializer.InternalCorsContainer.class);
            return container.toContainer();
        }

        return (CorsContainer) ctxt.handleUnexpectedToken(CorsContainer.class, jp);
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    static class  InternalCorsContainer {
        @JsonAlias("Access-Control-Allow-Origin")
        private String allowOrigin;

        @JsonAlias("Access-Control-Allow-Methods")
        private List<String> allowMethods;

        @JsonAlias("Access-Control-Allow-Headers")
        private List<String> allowHeaders;

        @JsonAlias("Access-Control-Max-Age")
        private LatencyContainer maxAge;

        @JsonAlias("Access-Control-Expose-Headers")
        private List<String> exposeHeaders;

        @JsonAlias("Access-Control-Allow-Credentials")
        private Boolean allowCredentials;

        public CorsContainer toContainer() {
            return CorsContainer.newContainer(allowOrigin, allowMethods, allowHeaders, maxAge, exposeHeaders, allowCredentials);
        }
    }
}
