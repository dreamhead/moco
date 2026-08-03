package com.github.dreamhead.moco.util;

import java.net.URL;
import java.util.Objects;

import static com.github.dreamhead.moco.util.Preconditions.checkArgument;

/**
 * Replacement for Guava's {@code com.google.common.io.Resources}.
 *
 * <p>The thread context class loader is consulted in preference to this class's own loader,
 * matching Guava. That ordering matters: Moco is embedded in other projects' test frameworks and
 * is run from the shrunk standalone jar, where the loader owning this class cannot always see the
 * caller's resources.
 */
public final class Resources {
    /**
     * Resolves a classpath resource, throwing {@link IllegalArgumentException} rather than
     * returning null when it is absent.
     */
    public static URL getResource(final String resourceName) {
        Objects.requireNonNull(resourceName, "Resource name should not be null");

        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        ClassLoader loader = contextClassLoader != null ? contextClassLoader : Resources.class.getClassLoader();

        URL url = loader.getResource(resourceName);
        checkArgument(url != null, "resource " + resourceName + " not found.");
        return url;
    }

    private Resources() {
    }
}
