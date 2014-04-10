package com.github.dreamhead.moco.resource;

import com.github.dreamhead.moco.ConfigApplier;
import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MocoConfig;

public class Resource implements Identifiable, ConfigApplier<Resource>, ResourceReader {
    private Identifiable identifiable;
    private ResourceConfigApplier configApplier;
    protected ResourceReader reader;

    public Resource(final Identifiable identifiable, final ResourceConfigApplier configApplier, final ResourceReader reader) {
        this.identifiable = identifiable;
        this.configApplier = configApplier;
        this.reader = reader;
    }

    @Override
    public Resource apply(final MocoConfig config) {
        return configApplier.apply(config, this);
    }

    @Override
    public String id() {
        return identifiable.id();
    }

    @Override
    public byte[] readFor(HttpRequest request) {
        return reader.readFor(request);
    }
}
