package com.github.dreamhead.moco.resource;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.resource.reader.ContentResourceReader;

public class ContentResource extends Resource implements Content {
    public ContentResource(final Identifiable identifiable, final ResourceConfigApplier configApplier, ContentResourceReader reader) {
        super(identifiable, configApplier, reader);
    }

    public String getContentType(HttpRequest request) {
        return ((ContentResourceReader)reader).getContentType(request);
    }
}
