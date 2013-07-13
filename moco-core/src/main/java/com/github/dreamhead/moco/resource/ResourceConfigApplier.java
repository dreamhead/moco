package com.github.dreamhead.moco.resource;

import com.github.dreamhead.moco.MocoConfig;

public interface ResourceConfigApplier {
    public Resource apply(MocoConfig config, Resource resource);
}
