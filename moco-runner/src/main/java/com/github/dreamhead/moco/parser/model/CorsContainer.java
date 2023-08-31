package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.dreamhead.moco.handler.cors.CorsConfig;
import com.github.dreamhead.moco.parser.deserializer.CookieContainerDeserializer;
import com.github.dreamhead.moco.parser.deserializer.CorsContainerDeserializer;
import com.google.common.base.MoreObjects;

import java.util.ArrayList;
import java.util.List;

import static com.github.dreamhead.moco.MocoCors.allowCredentials;
import static com.github.dreamhead.moco.MocoCors.allowHeaders;
import static com.github.dreamhead.moco.MocoCors.allowMethods;
import static com.github.dreamhead.moco.MocoCors.allowOrigin;
import static com.github.dreamhead.moco.MocoCors.exposeHeaders;
import static com.github.dreamhead.moco.MocoCors.maxAge;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonDeserialize(using = CorsContainerDeserializer.class)
public final class CorsContainer {
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

    public static CorsContainer newContainer() {
        return new CorsContainer();
    }

    public static CorsContainer newContainer(final String allowOrigin,
                                             final List<String> allowMethods,
                                             final List<String> allowHeaders,
                                             final LatencyContainer maxAge,
                                             final List<String> exposeHeaders,
                                             final Boolean allowCredentials) {
        CorsContainer container = new CorsContainer();
        container.allowOrigin = allowOrigin;
        container.allowMethods = allowMethods;
        container.allowHeaders = allowHeaders;
        container.maxAge = maxAge;
        container.exposeHeaders = exposeHeaders;
        container.allowCredentials = allowCredentials;
        return container;
    }

    public CorsConfig[] getConfigs() {
        List<CorsConfig> configs = new ArrayList<>();
        if (allowOrigin != null) {
            configs.add(allowOrigin(allowOrigin));
        }

        if (allowMethods != null) {
            configs.add(allowMethods(allowMethods.toArray(new String[0])));
        }

        if (allowHeaders != null) {
            configs.add(allowHeaders(allowHeaders.toArray(new String[0])));
        }

        if (maxAge != null) {
            configs.add(maxAge(maxAge.getLatency(), maxAge.getUnit()));
        }

        if (exposeHeaders != null) {
            configs.add(exposeHeaders(exposeHeaders.toArray(new String[0])));
        }

        if (allowCredentials != null) {
            configs.add(allowCredentials(allowCredentials));
        }

        return configs.toArray(new CorsConfig[0]);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("allowOrigin", allowOrigin)
                .add("allowMethods", allowMethods)
                .add("allowHeaders", allowHeaders)
                .add("maxAge", maxAge)
                .add("exposeHeaders", exposeHeaders)
                .add("allowCredentials", allowCredentials)
                .toString();
    }
}
