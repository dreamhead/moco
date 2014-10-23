package com.github.dreamhead.moco.bootstrap.arg;

import com.github.dreamhead.moco.bootstrap.ServerType;

public class HttpArgs extends StartArgs {
    protected HttpArgs(Integer port, Integer shutdownPort, String configurationFile, String globalSettings, String env) {
        super(ServerType.HTTP, port, shutdownPort, configurationFile, globalSettings, env, null);
    }

    public static Builder httpArgs() {
        return new Builder();
    }

    public static class Builder {
        private ServerType type;
        private Integer port;
        private Integer shutdownPort;
        private String configurationFile;
        private String settings;
        private String env;

        public Builder withPort(Integer port) {
            this.port = port;
            return this;
        }

        public Builder withShutdownPort(Integer shutdownPort) {
            this.shutdownPort = shutdownPort;
            return this;
        }

        public Builder withConfigurationFile(String configurationFile) {
            this.configurationFile = configurationFile;
            return this;
        }

        public Builder withSettings(String settings) {
            this.settings = settings;
            return this;
        }

        public Builder withEnv(String env) {
            this.env = env;
            return this;
        }

        public HttpArgs build() {
            return new HttpArgs(port, shutdownPort, configurationFile, settings, env);
        }
    }
}
