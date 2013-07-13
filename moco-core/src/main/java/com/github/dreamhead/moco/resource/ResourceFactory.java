package com.github.dreamhead.moco.resource;

import com.github.dreamhead.moco.util.Cookies;
import org.jboss.netty.handler.codec.http.HttpRequest;

import java.io.File;

import static com.github.dreamhead.moco.resource.ResourceConfigApplier.DO_NOTHING_APPLIER;
import static com.github.dreamhead.moco.resource.ResourceConfigApplierFactory.*;
import static com.github.dreamhead.moco.resource.TextId.id;

public class ResourceFactory {
    public static ContentResource textResource(final String text) {
        return new DefaultContentResource(id("text"), DO_NOTHING_APPLIER, new ContentResourceReader() {
            @Override
            public String getContentType() {
                return "text/plain; charset=UTF-8";
            }

            @Override
            public byte[] readFor(HttpRequest request) {
                return text.getBytes();
            }
        });
    }

    public static ContentResource fileResource(final File file) {
        String fileId = "file";
        return new DefaultContentResource(id(fileId), fileConfigApplier(fileId, file), new FileResourceReader(file));
    }

    public static ContentResource classpathFileResource(final String filename) {
        return new DefaultContentResource(id("pathresource"), DO_NOTHING_APPLIER, new ClasspathFileResourceReader(filename));
    }

    public static Resource methodResource(final String method) {
        return new DefaultResource(id("method"), DO_NOTHING_APPLIER, new ResourceReader() {
            @Override
            public byte[] readFor(HttpRequest request) {
                return method.toUpperCase().getBytes();
            }
        });
    }

    public static Resource versionResource(final Resource version) {
        return new DefaultResource(id("version"), DO_NOTHING_APPLIER, new ResourceReader() {
            @Override
            public byte[] readFor(HttpRequest request) {
                return version.readFor(request);
            }
        });
    }

    public static Resource headerResource(final String key, final Resource resource) {
        return new DefaultResource(id("header"), headerConfigApplier(key), new ResourceReader() {
            @Override
            public byte[] readFor(HttpRequest request) {
                return new Cookies().encodeCookie(key, new String(resource.readFor(request))).getBytes();
            }
        });
    }

    public static ContentResource templateResource(final ContentResource template) {
        return new DefaultContentResource(id("template"), templateConfigApplier(template), new TemplateResourceReader(template));
    }

    public static Resource uriResource(final String uri) {
        return new DefaultResource(id("uri"), uriConfigApplier(uri), new ResourceReader() {
            @Override
            public byte[] readFor(HttpRequest request) {
                return uri.getBytes();
            }
        });
    }

    private ResourceFactory() {}
}
