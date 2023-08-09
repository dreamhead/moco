package com.github.dreamhead.moco;

import com.github.dreamhead.moco.handler.MocoCorsHandler;
import com.github.dreamhead.moco.handler.cors.CorsConfig;
import com.github.dreamhead.moco.handler.cors.CorsCredentialsConfig;
import com.github.dreamhead.moco.handler.cors.CorsExposedHeadersConfig;
import com.github.dreamhead.moco.handler.cors.CorsHeadersConfig;
import com.github.dreamhead.moco.handler.cors.CorsMaxAgeConfig;
import com.github.dreamhead.moco.handler.cors.CorsMethodsConfig;
import com.github.dreamhead.moco.handler.cors.CorsOriginConfig;

public final class MocoCors {
    public static ResponseHandler cors(final CorsConfig... configs) {
        return new MocoCorsHandler(configs);
    }

    public static CorsConfig allowOrigin(final String origin) {
        return new CorsOriginConfig(origin);
    }

    public static CorsConfig allowMethods(final String... methods) {
        return new CorsMethodsConfig(methods);
    }

    public static CorsConfig allowHeaders(final String... headers) {
        return new CorsHeadersConfig(headers);
    }

    public static CorsConfig allowCredentials(final boolean allowed) {
        return new CorsCredentialsConfig(allowed);
    }

    public static CorsConfig exposeHeaders(final String... headers) {
        return new CorsExposedHeadersConfig(headers);
    }

    public static CorsConfig maxAge(final long maxAge) {
        return new CorsMaxAgeConfig(maxAge);
    }

    private MocoCors() {
    }
}
