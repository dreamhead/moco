package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.dreamhead.moco.resource.Resource;

import static com.github.dreamhead.moco.Moco.*;

public abstract class AbstractResource {
    protected TextContainer text;
    protected String file;
    @JsonProperty("path_resource")
    protected String pathResource;
    protected String version;

    public Resource retrieveResource() {
        if (text != null) {
            if (text.isRawText()) {
                return text(text.getText());
            }

            if ("template".equalsIgnoreCase(text.getOperation())) {
                return template(text.getText());
            }
        }

        if (file != null) {
            return file(file);
        }

        if (pathResource != null) {
            return pathResource(pathResource);
        }

        if (version != null) {
            return version(version);
        }

        return null;
    }
}
