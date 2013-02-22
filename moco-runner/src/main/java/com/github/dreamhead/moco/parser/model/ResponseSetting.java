package com.github.dreamhead.moco.parser.model;

import com.github.dreamhead.moco.handler.HeaderResponseHandler;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.handler.AndResponseHandler;
import com.github.dreamhead.moco.handler.ContentHandler;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.base.Function;
import com.google.common.base.Objects;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.github.dreamhead.moco.Moco.status;
import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.Lists.newArrayList;

public class ResponseSetting extends AbstractResource {
    protected String status;
    protected Map<String, String> headers;
    private CacheSetting cache;

    public String getStatus() {
        return status;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public CacheSetting getCache() {
        return cache;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("text", text)
                .add("file", file)
                .add("status", status)
                .add("headers", headers)
                .add("url", url)
                .add("cache", cache)
                .toString();
    }

    public boolean isResource() {
        return (text != null)
                || (file != null)
                || (url != null)
                || (cache != null);
    }

    @Override
    public Resource retrieveResource() {
        Resource resource = super.retrieveResource();
        if (resource != null) {
            return resource;
        }
        Resource cacheResource = cache.retrieveResource();
        if (cacheResource != null) {
            return cacheResource;
        }

        throw new IllegalArgumentException("unknown response setting with " + this);
    }

    public ResponseHandler getResponseHandler() {
        List<ResponseHandler> handlers = newArrayList();
        if (isResource()) {
            handlers.add(new ContentHandler(retrieveResource()));
        }

        if (status != null) {
            handlers.add(status(Integer.parseInt(status)));
        }

        if (headers != null) {
            Collection<ResponseHandler> collection = transform(headers.entrySet(), toHeaderResponseHandler());
            handlers.add(compositeResponseHandlers(collection));
        }

        if (handlers.isEmpty()) {
            throw new IllegalArgumentException("unknown response setting with " + this);
        }

        return handlers.size() == 1 ? handlers.get(0) : compositeResponseHandlers(handlers);
    }

    private ResponseHandler compositeResponseHandlers(Collection<ResponseHandler> collection) {
        ResponseHandler[] headerHandlers = collection.toArray(new ResponseHandler[collection.size()]);
        return new AndResponseHandler(headerHandlers);
    }

    private Function<Map.Entry<String, String>, ResponseHandler> toHeaderResponseHandler() {
        return new Function<Map.Entry<String, String>, ResponseHandler>() {
            @Override
            public ResponseHandler apply(Map.Entry<String, String> entry) {
                return new HeaderResponseHandler(entry.getKey(), entry.getValue());
            }
        };
    }
}
