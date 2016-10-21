package com.github.dreamhead.moco;

import com.github.dreamhead.moco.cookie.MaxAgeCookieOption;
import com.github.dreamhead.moco.cookie.PathCookieOption;
import com.github.dreamhead.moco.cookie.SecureCookieOption;

public abstract class CookieOption {
    public static CookieOption path(final String path) {
        return new PathCookieOption(path);
    }

    public static CookieOption maxAge(final long maxAge) {
        return new MaxAgeCookieOption(maxAge);
    }

    public static CookieOption secure(final boolean secure) {
        return new SecureCookieOption(secure);
    }
}
