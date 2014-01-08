package com.github.dreamhead.moco.mount;

import com.github.dreamhead.moco.MocoConfig;

import static com.github.dreamhead.moco.util.URLs.toBase;
import static com.google.common.base.Strings.nullToEmpty;

public class MountTo {
    private final String target;

    public MountTo(String target) {
        this.target = toBase(target);
    }

    public String extract(String uri) {
        return uri.startsWith(this.target) ? nullToEmpty(uri.replaceFirst(this.target, "")) : "";
    }

    public MountTo apply(MocoConfig config) {
        if (config.isFor("uri")) {
            return new MountTo(config.apply(this.target));
        }

        return this;
    }
}
