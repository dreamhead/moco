package com.github.dreamhead.moco.resource;

import com.github.dreamhead.moco.MocoConfig;

public interface ResourceConfigApplier {
    public Resource apply(MocoConfig config, Resource resource);

    ResourceConfigApplier DO_NOTHING_APPLIER = new ResourceConfigApplier() {
        @Override
        public Resource apply(MocoConfig config, Resource resource) {
            return resource;
        }
    };
}
