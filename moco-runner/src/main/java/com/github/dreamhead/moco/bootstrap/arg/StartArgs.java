package com.github.dreamhead.moco.bootstrap.arg;

import com.github.dreamhead.moco.HttpsCertificate;
import com.github.dreamhead.moco.bootstrap.HttpsArg;
import com.github.dreamhead.moco.bootstrap.ServerType;
import com.github.dreamhead.moco.bootstrap.ShutdownPortOption;
import com.google.common.base.Function;
import com.google.common.base.Optional;

import static com.google.common.base.Optional.fromNullable;

public class StartArgs extends ShutdownPortOption {
    private final ServerType type;
    private final Optional<Integer> port;
    private final Optional<String> configurationFile;
    private final Optional<String> settings;
    private final Optional<String> env;
    private final Optional<HttpsArg> httpsArg;

    protected StartArgs(ServerType type, Integer port, Integer shutdownPort, String configurationFile, String globalSettings, String env, HttpsArg httpsArg) {
        super(shutdownPort);
        this.type = type;
        this.port = fromNullable(port);
        this.configurationFile = fromNullable(configurationFile);
        this.settings = fromNullable(globalSettings);
        this.env = fromNullable(env);
        this.httpsArg = fromNullable(httpsArg);
    }

    public Optional<Integer> getPort() {
        return port;
    }

    public Optional<String> getConfigurationFile() {
        return configurationFile;
    }

    public boolean hasConfigurationFile() {
        return this.configurationFile.isPresent();
    }

    public Optional<String> getSettings() {
        return settings;
    }

    public Optional<String> getEnv() {
        return env;
    }

    public boolean isHttps() {
        return httpsArg.isPresent();
    }

    public Optional<HttpsCertificate> getHttpsCertificate() {
        return httpsArg.transform(toCertificate());
    }

    private Function<HttpsArg, HttpsCertificate> toCertificate() {
        return new Function<HttpsArg, HttpsCertificate>() {
            @Override
            public HttpsCertificate apply(HttpsArg input) {
                return input.getCertificate();
            }
        };
    }

    public static String help() {
        return "Moco Options:\\n[server type] -p port -c [configuration file]\\server type: http, https, socket";
    }

    public boolean isSocket() {
        return this.type == ServerType.SOCKET;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ServerType type;
        private Integer port;
        private Integer shutdownPort;
        private String configurationFile;
        private String settings;
        private String env;
        private HttpsArg httpsArg;

        public Builder withType(ServerType type) {
            this.type = type;
            return this;
        }

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

        public StartArgs build() {
            return new StartArgs(type, port, shutdownPort, configurationFile, settings, env, httpsArg);
        }
    }
}
