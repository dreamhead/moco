package com.github.dreamhead.moco.resource;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.resource.reader.ContentResourceReader;
import com.google.common.net.MediaType;

public class ContentResource extends Resource implements Content {
    public ContentResource(final Identifiable identifiable, final ResourceConfigApplier configApplier, ContentResourceReader reader) {
        super(identifiable, configApplier, reader);
    }

    public MediaType getContentType(final HttpRequest request) {
        return ((ContentResourceReader)reader).getContentType(request);
    }
}
