package com.github.dreamhead.moco.resource;

import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.resource.reader.*;
import com.github.dreamhead.moco.util.Cookies;
import com.github.dreamhead.moco.util.FileContentType;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

import java.io.File;

import static com.github.dreamhead.moco.resource.IdFactory.id;
import static com.github.dreamhead.moco.resource.ResourceConfigApplierFactory.*;

public class ResourceFactory {
    public static ContentResource textResource(final String text) {
        return contentResource(id("text"), DO_NOTHING_APPLIER, new ContentResourceReader() {
            @Override
            public String getContentType() {
                return FileContentType.DEFAULT_CONTENT_TYPE;
            }

            @Override
            public byte[] readFor(final Optional<? extends Request> request) {
                return text.getBytes();
            }
        });
    }

    public static ContentResource fileResource(final File file) {
        String fileId = "file";
        return contentResource(id(fileId), fileConfigApplier(fileId, file), new FileResourceReader(file));
    }

    public static ContentResource classpathFileResource(final String filename) {
        return contentResource(id("pathresource"), DO_NOTHING_APPLIER, new ClasspathFileResourceReader(filename));
    }

    public static Resource methodResource(final String method) {
        return resource(id("method"), DO_NOTHING_APPLIER, new ResourceReader() {
            @Override
            public byte[] readFor(Optional<? extends Request> request) {
                return method.toUpperCase().getBytes();
            }
        });
    }

    public static Resource versionResource(final Resource version) {
        return resource(id("version"), DO_NOTHING_APPLIER, new ResourceReader() {
            @Override
            public byte[] readFor(Optional<? extends Request> request) {
                return version.readFor(request);
            }
        });
    }

    public static Resource cookieResource(final String key, final Resource resource) {
        return resource(id("cookie"), cookieConfigApplier(key, resource), new ResourceReader() {
            @Override
            public byte[] readFor(Optional<? extends Request> request) {
                return new Cookies().encodeCookie(key, new String(resource.readFor(request))).getBytes();
            }
        });
    }

    public static ContentResource templateResource(final ContentResource template, ImmutableMap<String, String> variables) {
        return contentResource(id("template"), templateConfigApplier(template, variables), new TemplateResourceReader(template, variables));
    }

    public static Resource uriResource(final String uri) {
        return resource(id("uri"), uriConfigApplier("uri", uri), new ResourceReader() {
            @Override
            public byte[] readFor(Optional<? extends Request> request) {
                return uri.getBytes();
            }
        });
    }

    public static ContentResource customResource(final Function<Request, String> function) {
        CustomResourceReader reader = new CustomResourceReader(function);
        return contentResource(id("template"), DO_NOTHING_APPLIER, reader);
    }

    private static ContentResource contentResource(Identifiable id, ResourceConfigApplier applier, ContentResourceReader reader) {
        return new ContentResource(id, applier, reader);
    }

    private static Resource resource(Identifiable id, ResourceConfigApplier applier, ResourceReader reader) {
        return new Resource(id, applier, reader);
    }



    private ResourceFactory() {}
}
