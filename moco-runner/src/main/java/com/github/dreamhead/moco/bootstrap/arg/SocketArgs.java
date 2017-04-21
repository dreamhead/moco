package com.github.dreamhead.moco.bootstrap.arg;

import static com.github.dreamhead.moco.bootstrap.ServerType.SOCKET;

public final class SocketArgs extends StartArgs {
    private SocketArgs(final Integer port, final Integer shutdownPort, final String configurationFile,
                       final boolean watchService) {
        super(SOCKET, port, shutdownPort, configurationFile, null, null, null, watchService);
    }

    public static Builder socketArgs() {
        return new Builder();
    }

    public static class Builder {
        private Integer port;
        private Integer shutdownPort;
        private String configurationFile;
        private boolean watchService;

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

        public Builder withWatchService(final boolean watchService) {
            this.watchService = watchService;
            return this;
        }

        public SocketArgs build() {
            return new SocketArgs(port, shutdownPort, configurationFile, watchService);
        }
    }
}
