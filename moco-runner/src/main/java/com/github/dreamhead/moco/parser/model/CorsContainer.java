package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.dreamhead.moco.handler.cors.CorsConfig;

import java.util.ArrayList;
import java.util.List;

import static com.github.dreamhead.moco.MocoCors.allowHeaders;
import static com.github.dreamhead.moco.MocoCors.allowMethods;
import static com.github.dreamhead.moco.MocoCors.allowOrigin;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class CorsContainer {
    private String allowOrigin;
    private String allowMethods;

    private String allowHeaders;

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

        return configs.toArray(new CorsConfig[0]);
    }
}
