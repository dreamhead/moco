package com.github.dreamhead.moco.resource.reader;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.model.MessageContent;
import com.google.common.net.MediaType;

import java.nio.charset.Charset;
import java.util.function.Function;

import static com.github.dreamhead.moco.util.Jsons.toJson;

public final class JsonResourceReader implements ContentResourceReader {
    private Function<Request, Object> pojo;

    public JsonResourceReader(final Function<Request, Object> pojo) {
        this.pojo = pojo;
    }

    @Override
    public MediaType getContentType(final HttpRequest request) {
        return MediaType.create("application", "json").withCharset(Charset.defaultCharset());
    }

    @Override
    public MessageContent readFor(final Request request) {
        Object object = pojo.apply(request);
        if (object == null) {
            throw new NullPointerException("Null returned from json function");
        }
        return MessageContent.content().withContent(toJson(object)).build();
    }

    public Object getPojo() {
        return pojo.apply(null);
    }
}
