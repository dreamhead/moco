package com.github.dreamhead.moco.resource;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.resource.reader.ContentResourceReader;
import com.github.dreamhead.moco.resource.reader.JsonResourceReader;
import com.google.common.base.Optional;
import com.google.common.net.MediaType;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;

public final class ContentResource extends Resource implements Content {
    public ContentResource(final Identifiable identifiable, final ResourceConfigApplier configApplier,
                           final ContentResourceReader reader) {
        super(identifiable, configApplier, reader);
    }

    @Override
    public MediaType getContentType(final HttpRequest request) {
        return reader(ContentResourceReader.class).getContentType(request);
    }

    public Optional<Object> getJsonObject() {
        try {
            return of(reader(JsonResourceReader.class).getPojo());
        } catch (Exception e) {
            return absent();
        }
    }
}
