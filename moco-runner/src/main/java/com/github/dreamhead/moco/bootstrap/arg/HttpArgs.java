package com.github.dreamhead.moco.bootstrap.arg;

import com.github.dreamhead.moco.bootstrap.ServerType;

public final class HttpArgs extends StartArgs {
    private HttpArgs(final Integer port, final Integer shutdownPort,
                     final String[] configurationFiles, final String globalSettings,
                     final String env) {
        super(ServerType.HTTP, port, shutdownPort, configurationFiles, globalSettings, env, null);
    }

    public static Builder httpArgs() {
        return new Builder();
    }

    public static class Builder {
        private Integer port;
        private Integer shutdownPort;
        private String[] configurationFiles;
        private String settings;
        private String env;

        public Builder withPort(final Integer port) {
            this.port = port;
            return this;
        }

        public Builder withShutdownPort(final Integer shutdownPort) {
            this.shutdownPort = shutdownPort;
            return this;
        }

        public Builder withConfigurationFile(final String... configurationFiles) {
            this.configurationFiles = configurationFiles;
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

        public HttpArgs build() {
            return new HttpArgs(port, shutdownPort, configurationFiles, settings, env);
        }
    }
}
