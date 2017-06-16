package com.github.dreamhead.moco.bootstrap.arg;

import com.github.dreamhead.moco.HttpsCertificate;
import com.github.dreamhead.moco.bootstrap.HttpsArg;
import com.github.dreamhead.moco.bootstrap.ServerType;
import com.github.dreamhead.moco.bootstrap.ShutdownPortOption;
import com.google.common.base.Function;
import com.google.common.base.Optional;

import static com.google.common.base.Optional.fromNullable;

public abstract class StartArgs extends ShutdownPortOption {
    private final ServerType type;
    private final Optional<Integer> port;
    private final Optional<String[]> configurationFiles;
    private final Optional<String> settings;
    private final Optional<String> env;
    private final Optional<HttpsArg> httpsArg;

    protected StartArgs(final ServerType type, final Integer port, final Integer shutdownPort,
                        final String[] configurationFiles, final String globalSettings,
                        final String env, final HttpsArg httpsArg) {
        super(shutdownPort);
        this.type = type;
        this.port = fromNullable(port);
        this.configurationFiles = fromNullable(configurationFiles);
        this.settings = fromNullable(globalSettings);
        this.env = fromNullable(env);
        this.httpsArg = fromNullable(httpsArg);
    }

    public Optional<Integer> getPort() {
        return port;
    }

    public Optional<String[]> getConfigurationFiles() {
        return configurationFiles;
    }

    public boolean hasConfigurationFile() {
        return this.configurationFiles.isPresent();
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
            public HttpsCertificate apply(final HttpsArg input) {
                return input.getCertificate();
            }
        };
    }

    public static String help() {
        String separator = System.getProperty("line.separator");
        return "Moco Options:" + separator + "moco [server type] -p port -c [configuration file]" + separator + separator + "server type: http, https, socket";
    }

    public boolean isSocket() {
        return this.type == ServerType.SOCKET;
    }
}
