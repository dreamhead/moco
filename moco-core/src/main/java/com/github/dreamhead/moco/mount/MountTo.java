package com.github.dreamhead.moco.mount;

import static com.google.common.base.Strings.nullToEmpty;

public class MountTo {
    private final String target;

    public MountTo(String target) {
        if (!target.endsWith("/")) {
            this.target = target + "/";
        } else {
            this.target = target;
        }
    }

    public String extract(String uri) {
        if (!uri.startsWith(this.target)) {
            return null;
        }

        return nullToEmpty(uri.replaceFirst(this.target, ""));
    }
}
