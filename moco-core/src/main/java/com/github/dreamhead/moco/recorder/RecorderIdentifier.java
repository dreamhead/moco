package com.github.dreamhead.moco.recorder;

import com.github.dreamhead.moco.ConfigApplier;
import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.resource.ContentResource;
import com.github.dreamhead.moco.resource.Resource;

public class RecorderIdentifier implements RecorderConfig, ConfigApplier<RecorderIdentifier> {
    private ContentResource resource;

    public RecorderIdentifier(final ContentResource resource) {
        this.resource = resource;
    }

    public final String getIdentifier(final HttpRequest httpRequest) {
        return this.resource.readFor(httpRequest).toString();
    }

    @Override
    public final boolean isFor(final String name) {
        return IDENTIFIER.equalsIgnoreCase(name);
    }

    @Override
    public final RecorderIdentifier apply(final MocoConfig config) {
        Resource applied = resource.apply(config);
        if (applied != this.resource) {
            return new RecorderIdentifier((ContentResource) applied);
        }

        return this;
    }
}
