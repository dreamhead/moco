package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.github.dreamhead.moco.ResponseHandler;
import com.google.common.base.MoreObjects;

import java.util.stream.StreamSupport;

public final class CollectionContainer implements Container {
    private Iterable<ResponseSetting> collection;

    @JsonCreator
    public CollectionContainer(final Iterable<ResponseSetting> collection) {
        this.collection = collection;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("collection", collection)
                .toString();
    }

    public ResponseHandler[] toResponseHandlers() {
        return StreamSupport.stream(collection.spliterator(), false)
                .map(ResponseSetting::getResponseHandler)
                .toArray(ResponseHandler[]::new);
    }
}
