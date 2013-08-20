package com.github.dreamhead.moco.util;

import com.github.dreamhead.moco.ConfigApplier;
import com.github.dreamhead.moco.MocoConfig;

public class Configs {
    public static  <T extends ConfigApplier<T>> T configItem(T source, MocoConfig... configs) {
        if (source == null) {
            return null;
        }

        T target = source;
        for (MocoConfig config : configs) {
            target = target.apply(config);
        }
        return target;
    }

    private Configs() {}
}
