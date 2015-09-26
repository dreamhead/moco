package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class BaseResourceSetting {
    protected TextContainer text;
    protected FileContainer file;
    @JsonProperty("path_resource")
    protected FileContainer pathResource;
    protected Object json;

    public TextContainer getText() {
        return text;
    }

    public boolean hasText() {
        return text != null;
    }

    public FileContainer getFile() {
        return file;
    }

    public boolean hasFile() {
        return file != null;
    }

    public TextContainer getPathResource() {
        return pathResource;
    }

    public boolean hasPathResource() {
        return pathResource != null;
    }
}
