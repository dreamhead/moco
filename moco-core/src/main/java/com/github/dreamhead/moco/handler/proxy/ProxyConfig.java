package com.github.dreamhead.moco.handler.proxy;

import static com.google.common.base.Strings.nullToEmpty;

public class ProxyConfig {
    private final String localBase;
    private String remoteBase;

    public ProxyConfig(String localBase) {
        if (!localBase.endsWith("/")) {
            this.localBase = localBase + "/";
        } else {
            this.localBase = localBase;
        }
    }

    public ProxyConfig to(String remoteBase) {
        if (!localBase.endsWith("/")) {
            this.remoteBase = remoteBase + "/";
        } else {
            this.remoteBase = remoteBase;
        }
        return this;
    }

    public boolean canAccessedBy(String uri) {
        return uri.startsWith(localBase);
    }

    public String remoteUrl(String uri) {
        String relative = nullToEmpty(uri.replaceFirst(this.localBase, ""));
        return remoteBase + "/" + relative;
    }
}
