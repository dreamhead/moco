package com.github.dreamhead.moco.rest;

import com.github.dreamhead.moco.HttpsCertificate;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.MocoMonitor;
import com.github.dreamhead.moco.RestServer;
import com.github.dreamhead.moco.RestSetting;
import com.github.dreamhead.moco.internal.ActualHttpServer;
import com.github.dreamhead.moco.internal.InternalApis;
import com.google.common.base.Optional;

import static com.github.dreamhead.moco.util.URLs.resourceRoot;
import static com.google.common.base.Preconditions.checkNotNull;

public class ActualRestServer extends ActualHttpServer implements RestServer {
    public ActualRestServer(final Optional<Integer> port,
                               final Optional<HttpsCertificate> certificate,
                               final MocoMonitor monitor,
                               final MocoConfig... configs) {
        super(port, certificate, monitor, configs);
    }

    @Override
    public void resource(final String name, final RestSetting... settings) {
        checkNotNull(name, "Resource name should not be null");
        checkNotNull(settings, "Rest settings should not be null");

        this.request(InternalApis.context(resourceRoot(name))).response(new RestHandler(name, settings));
    }
}
