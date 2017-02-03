package com.github.dreamhead.moco.util;

import static com.google.common.base.Strings.isNullOrEmpty;

public final class StringUtil {
    public static String strip(final String text) {
        if (isNullOrEmpty(text)) {
            return "";
        }

        return text.trim();
    }

    private StringUtil() {
    }
}
