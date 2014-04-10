package com.github.dreamhead.moco.mount;

import com.github.dreamhead.moco.ConfigApplier;
import com.github.dreamhead.moco.MocoConfig;

import static com.github.dreamhead.moco.util.URLs.toBase;
import static com.google.common.base.Strings.nullToEmpty;

public class MountTo implements ConfigApplier<MountTo> {
    private final String target;

    public MountTo(final String target) {
        this.target = toBase(target);
    }

    public String extract(final String uri) {
        return uri.startsWith(this.target) ? nullToEmpty(uri.replaceFirst(this.target, "")) : "";
    }

    @Override
    @SuppressWarnings("unchecked")
    public MountTo apply(final MocoConfig config) {
        if (config.isFor(MocoConfig.URI_ID)) {
            return new MountTo((String)config.apply(this.target));
        }

        return this;
    }
}
