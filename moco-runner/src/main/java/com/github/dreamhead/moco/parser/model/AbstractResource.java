package com.github.dreamhead.moco.parser.model;

import com.github.dreamhead.moco.resource.Resource;

import static com.github.dreamhead.moco.Moco.file;
import static com.github.dreamhead.moco.Moco.text;
import static com.github.dreamhead.moco.Moco.url;

public abstract class AbstractResource {
    protected String text;
    protected String file;
    protected String url;

    public String getText() {
        return text;
    }

    public String getFile() {
        return file;
    }

    public String getUrl() {
        return url;
    }

    public Resource retrieveResource() {
        if (text != null) {
            return text(text);
        } else if (file != null) {
            return file(file);
        } else if (url != null) {
            return url(url);
        }

        return null;
    }
}
