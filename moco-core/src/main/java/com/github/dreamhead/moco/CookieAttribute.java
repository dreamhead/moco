package com.github.dreamhead.moco;

import com.github.dreamhead.moco.cookie.DomainCookieAttribute;
import com.github.dreamhead.moco.cookie.HttpOnlyAttribute;
import com.github.dreamhead.moco.cookie.MaxAgeCookieAttribute;
import com.github.dreamhead.moco.cookie.PathCookieAttribute;
import com.github.dreamhead.moco.cookie.SameSiteAttribute;
import com.github.dreamhead.moco.cookie.SecureCookieAttribute;
import io.netty.handler.codec.http.cookie.CookieHeaderNames;

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

    public static CookieAttribute sameSite(final String sameSite) {
        CookieHeaderNames.SameSite name = ofSameSite(sameSite);
        return new SameSiteAttribute(name);
    }

    public static CookieAttribute sameSite(final SameSite sameSite) {
        CookieHeaderNames.SameSite name = ofSameSite(sameSite.name());
        return new SameSiteAttribute(name);
    }

    public enum SameSite {
        STRICT,
        LAX,
        NONE
    }

    private static CookieHeaderNames.SameSite ofSameSite(final String name) {
        if (name != null) {
            for (CookieHeaderNames.SameSite each : CookieHeaderNames.SameSite.class.getEnumConstants()) {
                if (each.name().equalsIgnoreCase(name)) {
                    return each;
                }
            }
        }

        throw new IllegalArgumentException("Unknown SameSite value: " + name);
    }
}
