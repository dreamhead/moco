package com.github.dreamhead.moco.util;

import com.github.dreamhead.moco.ConfigApplier;
import com.github.dreamhead.moco.MocoConfig;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.stream.Collectors;

public final class Configs {
    public static <T extends ConfigApplier<T>> T configItem(final T source, final MocoConfig... configs) {
        if (source == null) {
            return null;
        }

        T target = source;
        for (MocoConfig config : configs) {
            target = target.apply(config);
        }
        return target;
    }

    public static <T extends ConfigApplier<T>> ImmutableList<T> configItems(final List<T> items,
                                                                            final MocoConfig... configs) {
        return items.stream()
                .map(item -> configItem(item, configs))
                .collect(ImmutableList.toImmutableList());
    }

    private Configs() {
    }
}
