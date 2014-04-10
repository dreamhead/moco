package com.github.dreamhead.moco.resource;

import com.github.dreamhead.moco.resource.reader.ContentResourceReader;

public class ContentResource extends Resource implements Content {
    public ContentResource(final Identifiable identifiable, final ResourceConfigApplier configApplier, ContentResourceReader reader) {
        super(identifiable, configApplier, reader);
    }

    public String getContentType() {
        return ((ContentResourceReader)reader).getContentType();
    }
}
