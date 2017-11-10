package com.github.dreamhead.moco.resource.reader;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.model.MessageContent;
import com.google.common.base.Optional;
import com.google.common.net.MediaType;

import static com.github.dreamhead.moco.util.Jsons.toJson;

public class JsonResourceReader implements ContentResourceReader {
    private Object pojo;

    public JsonResourceReader(final Object pojo) {
        this.pojo = pojo;
    }

    @Override
    public MediaType getContentType(HttpRequest request) {
        return MediaType.create("application", "json");
    }

    @Override
    public MessageContent readFor(Optional<? extends Request> request) {
        return MessageContent.content().withContent(toJson(pojo)).build();
    }
}
