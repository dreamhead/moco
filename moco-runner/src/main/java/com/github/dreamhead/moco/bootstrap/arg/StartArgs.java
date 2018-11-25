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
    private final Optional<String> configurationFile;
    private final Optional<String> settings;
    private final Optional<String> env;
    private final Optional<HttpsArg> httpsArg;

    protected StartArgs(final ServerType type, final Integer port, final Integer shutdownPort,
                        final String configurationFile, final String globalSettings,
                        final String env, final HttpsArg httpsArg) {
        super(shutdownPort);
        this.type = type;
        this.port = fromNullable(port);
        this.configurationFile = fromNullable(configurationFile);
        this.settings = fromNullable(globalSettings);
        this.env = fromNullable(env);
        this.httpsArg = fromNullable(httpsArg);
    }

    public final Optional<Integer> getPort() {
        return port;
    }

    public final Optional<String> getConfigurationFile() {
        return configurationFile;
    }

    public final boolean hasConfigurationFile() {
        return this.configurationFile.isPresent();
    }

    public final Optional<String> getSettings() {
        return settings;
    }

    public final Optional<String> getEnv() {
        return env;
    }

    public final boolean isHttps() {
        return httpsArg.isPresent();
    }

    public final Optional<HttpsCertificate> getHttpsCertificate() {
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

    public final boolean isSocket() {
        return this.type == ServerType.SOCKET;
    }
}
