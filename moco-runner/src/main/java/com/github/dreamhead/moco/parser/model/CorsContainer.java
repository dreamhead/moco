package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.dreamhead.moco.handler.cors.CorsConfig;
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
public final class CorsContainer {
    @JsonAlias("Access-Control-Allow-Origin")
    private String allowOrigin;

    @JsonAlias("Access-Control-Allow-Methods")
    private List<String> allowMethods;

    @JsonAlias("Access-Control-Allow-Headers")
    private List<String> allowHeaders;

    @JsonAlias("Access-Control-Max-Age")
    private Long maxAge;

    @JsonAlias("Access-Control-Expose-Headers")
    private List<String> exposeHeaders;

    @JsonAlias("Access-Control-Allow-Credentials")
    private Boolean allowCredentials;

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
            configs.add(maxAge(maxAge));
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
