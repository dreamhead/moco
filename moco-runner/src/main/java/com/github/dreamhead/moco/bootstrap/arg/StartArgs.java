package com.github.dreamhead.moco.bootstrap.arg;

import com.github.dreamhead.moco.HttpsCertificate;
import com.github.dreamhead.moco.bootstrap.HttpsArg;
import com.github.dreamhead.moco.bootstrap.ServerType;
import com.github.dreamhead.moco.bootstrap.ShutdownPortOption;

import java.util.Optional;

import static java.util.Optional.ofNullable;


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
        this.port = ofNullable(port);
        this.configurationFile = ofNullable(configurationFile);
        this.settings = ofNullable(globalSettings);
        this.env = ofNullable(env);
        this.httpsArg = ofNullable(httpsArg);
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
        return httpsArg.map(HttpsArg::getCertificate);
    }

    public static String help() {
        String separator = System.getProperty("line.separator");
        return "Moco Options:" + separator + "moco [server type] -p port -c [configuration file]" + separator + separator + "server type: http, https, socket";
    }

    public final boolean isSocket() {
        return this.type == ServerType.SOCKET;
    }
}
