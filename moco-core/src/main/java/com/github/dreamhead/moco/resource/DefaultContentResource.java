package com.github.dreamhead.moco.resource;

public class DefaultContentResource extends DefaultResource implements ContentResource {
    public DefaultContentResource(Identifiable identifiable, ResourceConfigApplier configApplier, ContentResourceReader reader) {
        super(identifiable, configApplier, reader);
    }

    @Override
    public String getContentType() {
        return ((ContentResourceReader)reader).getContentType();
    }
}
