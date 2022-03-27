package com.github.dreamhead.moco.bootstrap.arg;

import static com.github.dreamhead.moco.bootstrap.ServerType.SOCKET;

public final class SocketArgs extends StartArgs {
    private SocketArgs(final Integer port, final Integer shutdownPort, final String configurationFile, boolean quiet) {
        super(SOCKET, port, shutdownPort, configurationFile, null, null, quiet, null);
    }

    public static Builder socketArgs() {
        return new Builder();
    }

    public static class Builder {
        private Integer port;
        private Integer shutdownPort;
        private String configurationFile;
        private boolean quiet;

        public final Builder withPort(final Integer port) {
            this.port = port;
            return this;
        }

        public final Builder withShutdownPort(final Integer shutdownPort) {
            this.shutdownPort = shutdownPort;
            return this;
        }

        public final Builder withConfigurationFile(final String configurationFile) {
            this.configurationFile = configurationFile;
            return this;
        }

        public final SocketArgs build() {
            return new SocketArgs(port, shutdownPort, configurationFile, quiet);
        }

        public Builder withQuiet(final boolean quiet) {
            this.quiet = quiet;
            return this;
        }
    }
}
