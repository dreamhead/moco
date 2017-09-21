package com.github.dreamhead.moco.config;

import com.github.dreamhead.moco.MocoConfig;

import static com.github.dreamhead.moco.util.Files.join;

public final class MocoFileRootConfig implements MocoConfig<String> {
    private final String fileRoot;

    public MocoFileRootConfig(final String fileRoot) {
        this.fileRoot = fileRoot;
    }

    @Override
    public boolean isFor(final String id) {
        return FILE_ID.equalsIgnoreCase(id);
    }

    @Override
    public String apply(final String filename) {
        return join(this.fileRoot, filename);
    }
}
