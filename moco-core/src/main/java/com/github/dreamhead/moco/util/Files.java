package com.github.dreamhead.moco.util;

import java.io.File;

public class Files {
    public static String join(String path1, String path2, String... paths) {
        String finalPath = actualJoin(path1, path2);
        for (String path : paths) {
            finalPath = actualJoin(finalPath, path);
        }

        return finalPath;
    }

    private static String actualJoin(String path1, String path2) {
        File file1 = new File(path1);
        File file2 = new File(file1, path2);
        return file2.getPath();
    }

    private Files() {}
}
