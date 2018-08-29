package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.github.dreamhead.moco.ResponseHandler;
import com.google.common.base.Function;
import com.google.common.base.MoreObjects;

import static com.google.common.collect.FluentIterable.from;

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
        return from(collection).transform(toResponseHandler()).toArray(ResponseHandler.class);
    }

    private Function<ResponseSetting, ResponseHandler> toResponseHandler() {
        return new Function<ResponseSetting, ResponseHandler>() {
            @Override
            public ResponseHandler apply(final ResponseSetting setting) {
                return setting.getResponseHandler();
            }
        };
    }
}
