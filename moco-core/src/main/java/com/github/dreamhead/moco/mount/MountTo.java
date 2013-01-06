package com.github.dreamhead.moco.mount;

public class MountTo {
    private String target;

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

        String result = uri.replaceFirst(this.target, "");
        return result.length() == 0 ? null : result;
    }
}
