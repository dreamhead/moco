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
        return uri.startsWith(this.target) ? nullToEmpty(uri.replaceFirst(this.target, "")) : "";
    }
}
