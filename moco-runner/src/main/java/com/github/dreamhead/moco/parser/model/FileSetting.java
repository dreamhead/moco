package com.github.dreamhead.moco.parser.model;

import com.github.dreamhead.moco.resource.WritableResource;
import static com.github.dreamhead.moco.Moco.file;

public class FileSetting {
    private String file;

    public String getFile() {
        return file;
    }

    public WritableResource retrieveResource() {
        return file(file);
    }
}
