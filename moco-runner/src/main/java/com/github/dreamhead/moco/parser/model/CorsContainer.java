package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.dreamhead.moco.handler.cors.CorsConfig;

import java.util.ArrayList;
import java.util.List;

import static com.github.dreamhead.moco.MocoCors.allowOrigin;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class CorsContainer {
    private String allowOrigin;

    public CorsConfig[] getConfigs() {
        List<CorsConfig> configs = new ArrayList<>();
        if (allowOrigin != null) {
            configs.add(allowOrigin(allowOrigin));
        }
        return configs.toArray(new CorsConfig[0]);
    }
}
