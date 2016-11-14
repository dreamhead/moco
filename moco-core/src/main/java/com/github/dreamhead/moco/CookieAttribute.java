package com.github.dreamhead.moco;

import com.github.dreamhead.moco.cookie.DomainCookieAttribute;
import com.github.dreamhead.moco.cookie.HttpOnlyAttribute;
import com.github.dreamhead.moco.cookie.MaxAgeCookieAttribute;
import com.github.dreamhead.moco.cookie.PathCookieAttribute;
import com.github.dreamhead.moco.cookie.SecureCookieAttribute;

import java.util.concurrent.TimeUnit;

public abstract class CookieAttribute {
    public static CookieAttribute path(final String path) {
        return new PathCookieAttribute(path);
    }

    public static CookieAttribute domain(final String domain) {
        return new DomainCookieAttribute(domain);
    }

    public static CookieAttribute maxAge(final long maxAge, final TimeUnit unit) {
        return new MaxAgeCookieAttribute(maxAge, unit);
    }

    public static CookieAttribute secure() {
        return new SecureCookieAttribute();
    }

    public static CookieAttribute httpOnly() {
        return new HttpOnlyAttribute();
    }
}
