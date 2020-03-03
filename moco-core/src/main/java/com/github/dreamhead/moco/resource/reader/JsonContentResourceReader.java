package com.github.dreamhead.moco.resource.reader;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.net.MediaType;

import java.nio.charset.Charset;

public class JsonContentResourceReader implements ContentResourceReader {
    private Resource resource;

    public JsonContentResourceReader(final Resource resource) {
        this.resource = resource;
    }

    @Override
    public MediaType getContentType(final HttpRequest request) {
        return MediaType.create("application", "json").withCharset(Charset.defaultCharset());
    }

    @Override
    public MessageContent readFor(final Request request) {
        return resource.readFor(request);
    }
}
