package com.github.dreamhead.moco.bootstrap.arg;

import com.github.dreamhead.moco.bootstrap.HttpsArg;
import com.github.dreamhead.moco.bootstrap.ServerType;

public class HttpsArgs extends StartArgs {
    protected HttpsArgs(Integer port, Integer shutdownPort, String configurationFile, String globalSettings, String env, HttpsArg httpsArg) {
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

        public Builder withHttpsArg(HttpsArg httpsArg) {
            this.httpsArg = httpsArg;
            return this;
        }

        public HttpsArgs build() {
            return new HttpsArgs(port, shutdownPort, configurationFile, settings, env, httpsArg);
        }
    }
}
