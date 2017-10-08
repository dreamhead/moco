package com.github.dreamhead.moco.util;

import com.google.common.base.Function;

import java.io.File;

import static com.github.dreamhead.moco.util.Preconditions.checkNotNullOrEmpty;
import static com.google.common.base.Preconditions.checkNotNull;

public final class Files {
    public static String join(final String path1, final String path2, final String... paths) {
        String finalPath = actualJoin(path1, path2);
        for (String path : paths) {
            finalPath = actualJoin(finalPath, path);
        }

        return finalPath;
    }

    private static String actualJoin(final String path1, final String path2) {
        return joinFiles(path1, path2).getPath();
    }

    private static File joinFiles(final String path1, final String path2) {
        checkNotNullOrEmpty(path2, "Empty path is not allowed");

        if (path1 == null) {
            return new File(path2);
        }

        return new File(new File(path1), path2);
    }

    public static File directoryOf(final File file) {
        checkNotNull(file);
        File parentFile = file.getParentFile();
        if (parentFile == null) {
            return new File(".");
        }

        return parentFile;
    }

    public static Function<String, File> filenameToFile() {
        return new Function<String, File>() {
            @Override
            public File apply(final String input) {
                return new File(input);
            }
        };
    }

    private Files() {
    }
}
