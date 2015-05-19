package com.github.dreamhead.moco.resource;

import com.github.dreamhead.moco.MocoConfig;

public interface ResourceConfigApplier {
    Resource apply(final MocoConfig config, final Resource resource);
}
