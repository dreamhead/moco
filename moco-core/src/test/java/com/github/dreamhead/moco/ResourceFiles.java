package com.github.dreamhead.moco;

import com.google.common.io.Files;
import com.google.common.io.InputSupplier;
import com.google.common.io.Resources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public final class ResourceFiles {
    private ResourceFiles() {}

    public static File newTempResourceFile(String resPath) throws IOException {
        File resFile = File.createTempFile("res.", ".tmp");
        Files.copy(resourceInputSupplier(resPath), resFile);

        return resFile;
    }

    public static File newResourceFile(String resPath, File parent) throws IOException {
        File resFile = new File(parent, guessFileName(resPath));
        Files.copy(resourceInputSupplier(resPath), resFile);
        return resFile;
    }

    private static String guessFileName(String resPath) {
        String[] parts = resPath.split("/");
        return parts[parts.length - 1];
    }

    private static InputSupplier<InputStream> resourceInputSupplier(final String resPath) {
        return new InputSupplier<InputStream>() {
            @Override
            public InputStream getInput() throws IOException {
                return Resources.getResource(resPath).openStream();
            }
        };
    }
}
