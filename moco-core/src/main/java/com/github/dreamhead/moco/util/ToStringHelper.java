package com.github.dreamhead.moco.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Replacement for Guava's {@code MoreObjects.ToStringHelper}, rendering
 * {@code SimpleName{key=value, key=value}}.
 *
 * <p>This has to be a real type rather than inlined string building, because
 * {@code DefaultHttpMessage} exposes {@code protected ToStringHelper toStringHelper()} and
 * subclasses extend the chain through {@code super.toStringHelper().add(...)}.
 */
public final class ToStringHelper {
    private final String name;
    private final List<String> values = new ArrayList<>();
    private boolean omitNullValues;

    private ToStringHelper(final String name) {
        this.name = name;
    }

    public static ToStringHelper of(final Object self) {
        return new ToStringHelper(self.getClass().getSimpleName());
    }

    public ToStringHelper omitNullValues() {
        this.omitNullValues = true;
        return this;
    }

    public ToStringHelper add(final String name, final Object value) {
        if (this.omitNullValues && value == null) {
            return this;
        }

        this.values.add(name + "=" + asString(value));
        return this;
    }

    // Guava renders arrays through deepToString rather than as an identity hash.
    private static String asString(final Object value) {
        if (value == null || !value.getClass().isArray()) {
            return String.valueOf(value);
        }

        String rendered = Arrays.deepToString(new Object[]{value});
        return rendered.substring(1, rendered.length() - 1);
    }

    @Override
    public String toString() {
        return this.name + "{" + String.join(", ", this.values) + "}";
    }
}
