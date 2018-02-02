package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.parser.deserializer.CollectionContainerDeserializer;
import com.google.common.base.Function;
import com.google.common.base.MoreObjects;

import static com.google.common.collect.FluentIterable.from;

@JsonDeserialize(using = CollectionContainerDeserializer.class)
public class CollectionContainer implements Container {
    private Iterable<ResponseSetting> collection;

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
