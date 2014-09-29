package com.github.dreamhead.moco.bootstrap;

import com.github.dreamhead.moco.MocoVersion;

public class VersionTask implements BootstrapTask {
    @Override
    public void run(String[] args) {
        System.out.println(MocoVersion.VERSION);
    }
}
