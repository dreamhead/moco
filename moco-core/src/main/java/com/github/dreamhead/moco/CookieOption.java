package com.github.dreamhead.moco;

import com.github.dreamhead.moco.cookie.DomainCookieOption;
import com.github.dreamhead.moco.cookie.HttpOnlyOption;
import com.github.dreamhead.moco.cookie.MaxAgeCookieOption;
import com.github.dreamhead.moco.cookie.PathCookieOption;
import com.github.dreamhead.moco.cookie.SecureCookieOption;

import java.util.concurrent.TimeUnit;

public abstract class CookieOption {
    public static CookieOption path(final String path) {
        return new PathCookieOption(path);
    }

    public static CookieOption domain(final String domain) {
        return new DomainCookieOption(domain);
    }

    public static CookieOption maxAge(final long maxAge, final TimeUnit unit) {
        return new MaxAgeCookieOption(maxAge, unit);
    }

    public static CookieOption secure() {
        return new SecureCookieOption();
    }

    public static CookieOption httpOnly() {
        return new HttpOnlyOption();
    }
}
