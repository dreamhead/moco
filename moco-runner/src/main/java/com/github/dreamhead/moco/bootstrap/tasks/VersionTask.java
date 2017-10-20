package com.github.dreamhead.moco.bootstrap.tasks;

import com.github.dreamhead.moco.MocoVersion;
import com.github.dreamhead.moco.bootstrap.BootstrapTask;

public final class VersionTask implements BootstrapTask {
    @Override
    public void run(final String[] args) {
        System.out.println(MocoVersion.VERSION);
    }
}
