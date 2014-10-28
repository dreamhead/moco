package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class AttachmentSetting {
    private String filename;
    private TextContainer text;
    private TextContainer file;
    @JsonProperty("path_resource")
    private TextContainer pathResource;

    public String getFilename() {
        return filename;
    }

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
