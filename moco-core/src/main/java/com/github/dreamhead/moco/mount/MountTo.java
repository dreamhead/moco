package com.github.dreamhead.moco.mount;

import com.github.dreamhead.moco.ConfigApplier;
import com.github.dreamhead.moco.MocoConfig;
import com.google.common.base.Optional;

import static com.github.dreamhead.moco.util.URLs.toBase;
import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.fromNullable;

public final class MountTo implements ConfigApplier<MountTo> {
    private final String target;

    public MountTo(final String target) {
        this.target = toBase(target);
    }

    public Optional<String> extract(final String uri) {
        if (uri.startsWith(this.target) && uri.length() != this.target.length()) {
            return fromNullable(uri.replaceFirst(this.target, ""));
        }

        return absent();
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
