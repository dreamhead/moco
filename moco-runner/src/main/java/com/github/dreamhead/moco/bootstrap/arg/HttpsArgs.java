package com.github.dreamhead.moco.bootstrap.arg;

import com.github.dreamhead.moco.bootstrap.HttpsArg;
import com.github.dreamhead.moco.bootstrap.ServerType;

public final class HttpsArgs extends StartArgs {
    private HttpsArgs(final Integer port, final Integer shutdownPort, final String configurationFile,
                        final String globalSettings, final String env, final HttpsArg httpsArg) {
        super(ServerType.HTTPS, port, shutdownPort, configurationFile, globalSettings, env, httpsArg);
    }

    public static Builder httpsArgs() {
        return new Builder();
    }

    public static class Builder {
        private Integer port;
        private Integer shutdownPort;
        private String configurationFile;
        private String settings;
        private String env;
        private HttpsArg httpsArg;

        public Builder withPort(final Integer port) {
            this.port = port;
            return this;
        }

        public Builder withShutdownPort(final Integer shutdownPort) {
            this.shutdownPort = shutdownPort;
            return this;
        }

        public Builder withConfigurationFile(final String configurationFile) {
            this.configurationFile = configurationFile;
            return this;
        }

        public Builder withSettings(final String settings) {
            this.settings = settings;
            return this;
        }

        public Builder withEnv(final String env) {
            this.env = env;
            return this;
        }

        public Builder withHttpsArg(final HttpsArg httpsArg) {
            this.httpsArg = httpsArg;
            return this;
        }

        public HttpsArgs build() {
            return new HttpsArgs(port, shutdownPort, configurationFile, settings, env, httpsArg);
        }
    }
}
