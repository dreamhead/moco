package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.dreamhead.moco.resource.ContentResource;

import static com.github.dreamhead.moco.Moco.*;

public abstract class AbstractResource {
    protected String text;
    protected String file;
    protected String url;
    @JsonProperty("path_resource")
    protected String pathResource;

    public ContentResource retrieveResource() {
        if (text != null) {
            return text(text);
        }

        if (file != null) {
            return file(file);
        }

        if (url != null) {
            return url(url);
        }

        if (pathResource != null) {
            return pathResource(pathResource);
        }

        return null;
    }
}
