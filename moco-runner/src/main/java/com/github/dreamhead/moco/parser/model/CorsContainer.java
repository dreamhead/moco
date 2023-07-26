package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.dreamhead.moco.handler.cors.CorsConfig;

import java.util.ArrayList;
import java.util.List;

import static com.github.dreamhead.moco.MocoCors.allowHeaders;
import static com.github.dreamhead.moco.MocoCors.allowMethods;
import static com.github.dreamhead.moco.MocoCors.allowOrigin;
import static com.github.dreamhead.moco.MocoCors.exposeHeaders;
import static com.github.dreamhead.moco.MocoCors.maxAge;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class CorsContainer {
    @JsonAlias("Access-Control-Allow-Origin")
    private String allowOrigin;

    @JsonAlias("Access-Control-Allow-Methods")
    private String allowMethods;

    @JsonAlias("Access-Control-Allow-Headers")
    private String allowHeaders;

    @JsonAlias("Access-Control-Max-Age")
    private Long maxAge;

    @JsonAlias("Access-Control-Expose-Headers")
    private String exposeHeaders;

    public CorsConfig[] getConfigs() {
        List<CorsConfig> configs = new ArrayList<>();
        if (allowOrigin != null) {
            configs.add(allowOrigin(allowOrigin));
        }

        if (allowMethods != null) {
            configs.add(allowMethods(allowMethods.split(",")));
        }

        if (allowHeaders != null) {
            configs.add(allowHeaders(allowHeaders.split(",")));
        }

        if (maxAge != null) {
            configs.add(maxAge(maxAge));
        }

        if (exposeHeaders != null) {
            configs.add(exposeHeaders(exposeHeaders.split(",")));
        }

        return configs.toArray(new CorsConfig[0]);
    }
}
