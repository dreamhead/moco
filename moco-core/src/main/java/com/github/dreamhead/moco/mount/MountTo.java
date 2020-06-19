package com.github.dreamhead.moco.mount;

import com.github.dreamhead.moco.ConfigApplier;
import com.github.dreamhead.moco.MocoConfig;

import java.util.Optional;

import static com.github.dreamhead.moco.util.URLs.toBase;
import static java.util.Optional.empty;

public final class MountTo implements ConfigApplier<MountTo> {
    private final String target;

    public MountTo(final String target) {
        this.target = toBase(target);
    }

    public Optional<String> extract(final String uri) {
        if (uri.startsWith(this.target) && uri.length() != this.target.length()) {
            return Optional.of(uri.replaceFirst(this.target, ""));
        }

        return empty();
    }

    @Override
    @SuppressWarnings("unchecked")
    public MountTo apply(final MocoConfig config) {
        if (config.isFor(MocoConfig.URI_ID)) {
            return new MountTo((String) config.apply(this.target));
        }

        return this;
    }
}
