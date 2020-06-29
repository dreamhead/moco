package com.github.dreamhead.moco.recorder;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MocoException;
import com.github.dreamhead.moco.util.Jsons;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RecorderTape implements RecorderConfig {
    private final Path path;

    public RecorderTape(final String path) {
        this.path = Paths.get(path);
    }

    public final void write(final String name, final HttpRequest httpRequest) {
        TapeContent content = getTapeContent();
        content.addRequest(name, httpRequest);

        try {
            String result = Jsons.toJson(content);
            Files.write(path, result.getBytes());
        } catch (IOException e) {
            throw new MocoException(e);
        }
    }

    private TapeContent getTapeContent() {
        try {
            BufferedReader reader = Files.newBufferedReader(path);
            if (reader.ready()) {
                return Jsons.toObject(reader, TapeContent.class);
            }

            return new TapeContent();
        } catch (FileNotFoundException | NoSuchFileException e) {
            return new TapeContent();
        } catch (IOException e) {
            throw new MocoException(e);
        }
    }

    public final HttpRequest read(final String name) {
        return getTapeContent().getRequest(name);
    }

    @Override
    public final boolean isFor(final String name) {
        return TAPE.equalsIgnoreCase(name);
    }
}
