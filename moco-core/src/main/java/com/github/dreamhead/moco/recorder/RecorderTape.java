package com.github.dreamhead.moco.recorder;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MocoException;
import com.github.dreamhead.moco.util.Jsons;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RecorderTape {
    private Path path;

    public RecorderTape(final String path) {
        this.path = Paths.get(path);
    }

    public void write(String name, final HttpRequest httpRequest) {
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
            InputStream inputStream = new FileInputStream(path.toFile());
            if (inputStream.available() <= 0) {
                return new TapeContent();
            }
            return Jsons.toObject(inputStream, TapeContent.class);
        } catch (FileNotFoundException e) {
            return new TapeContent();
        } catch (IOException e) {
            throw new MocoException(e);
        }
    }

    public HttpRequest read(String name) {
        return getTapeContent().getRequest(name);
    }
}
