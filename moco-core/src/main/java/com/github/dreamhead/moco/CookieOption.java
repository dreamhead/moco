package com.github.dreamhead.moco;

import com.github.dreamhead.moco.cookie.PathCookieOption;

public abstract class CookieOption {
    public static CookieOption path(final String path) {
        return new PathCookieOption(path);
    }
}
