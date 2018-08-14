package com.github.dreamhead.moco.resource;

import com.github.dreamhead.moco.MocoConfig;

public interface ResourceConfigApplier {
    Resource apply(MocoConfig config, Resource resource);
}
