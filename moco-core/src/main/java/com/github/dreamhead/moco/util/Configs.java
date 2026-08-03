package com.github.dreamhead.moco.util;

import com.github.dreamhead.moco.ConfigApplier;
import com.github.dreamhead.moco.MocoConfig;

import java.util.List;

import static java.util.Objects.requireNonNull;

public final class Configs {
    public static <T extends ConfigApplier<T>> T configItem(final T source, final MocoConfig<?>... configs) {
        if (source == null) {
            return null;
        }

        T target = source;
        for (MocoConfig<?> config : configs) {
            target = target.apply(config);
        }
        return target;
    }

    public static <T extends ConfigApplier<T>> List<T> configItems(final List<T> items,
                                                                            final MocoConfig<?>... configs) {
        requireNonNull(items, "config items should not be null");
        return items.stream()
                .map(item -> configItem(item, configs))
                .toList();
    }

    private Configs() {
    }
}
