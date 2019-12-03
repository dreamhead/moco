package com.github.dreamhead.moco.recorder;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MocoException;
import com.github.dreamhead.moco.model.DefaultHttpRequest;
import com.github.dreamhead.moco.util.Jsons;

import java.io.FileInputStream;
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

    public void write(final HttpRequest httpRequest) {
        String content = Jsons.toJson(httpRequest);
        try {
            Files.write(path, content.getBytes());
        } catch (IOException e) {
            throw new MocoException(e);
        }
    }

    public HttpRequest read() {
        try {
            InputStream inputStream = new FileInputStream(path.toFile());
            return Jsons.toObject(inputStream, DefaultHttpRequest.class);
        } catch (IOException e) {
            throw new MocoException(e);
        }
    }
}
