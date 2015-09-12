package com.github.dreamhead.moco.resource;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.resource.reader.ContentResourceReader;
import com.google.common.net.MediaType;

public class ContentResource extends Resource implements Content {
    public ContentResource(final Identifiable identifiable, final ResourceConfigApplier configApplier,
                           final ContentResourceReader reader) {
        super(identifiable, configApplier, reader);
    }

    @Override
    public MediaType getContentType(final HttpRequest request) {
        return reader(ContentResourceReader.class).getContentType(request);
    }
}
