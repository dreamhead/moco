package com.github.dreamhead.moco;

import com.github.dreamhead.moco.monitor.QuietMonitor;
import com.github.dreamhead.moco.rest.ActualRestServer;
import com.github.dreamhead.moco.rest.RestSetting;
import com.google.common.base.Optional;

import static com.google.common.base.Optional.of;

public final class MocoRest {
    public static RestServer restServer(final int port, final MocoConfig... configs) {
        return new ActualRestServer(of(port), Optional.<HttpsCertificate>absent(), new QuietMonitor(), configs);
    }

    public static RestSetting get(final String id, final ResponseHandler handler) {
        return new RestSetting(id, handler);
    }

    private MocoRest() {
    }
}
