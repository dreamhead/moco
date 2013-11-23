package com.github.dreamhead.moco.util;

import com.github.dreamhead.moco.ConfigApplier;
import com.github.dreamhead.moco.MocoConfig;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;

import java.util.List;

import static com.google.common.collect.FluentIterable.from;

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

    public static <T extends ConfigApplier> ImmutableList<T> configItems(List<T> items, MocoConfig... configs) {
        @SuppressWarnings("unchecked")
        Function<T, T> config = config(configs);
        return from(items).transform(config).toList();
    }

    private static <T extends ConfigApplier<T>> Function<T, T> config(final MocoConfig... configs) {
        return new Function<T, T>() {
            @Override
            public T apply(T item) {
                return configItem(item, configs);
            }
        };
    }

    private Configs() {}
}
