package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class BaseResourceSetting {
    protected TextContainer text;
    protected TextContainer file;
    @JsonProperty("path_resource")
    protected TextContainer pathResource;

    public TextContainer getText() {
        return text;
    }

    public boolean hasText() {
        return text != null;
    }

    public TextContainer getFile() {
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
