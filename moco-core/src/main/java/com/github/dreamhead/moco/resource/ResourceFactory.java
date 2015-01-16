package com.github.dreamhead.moco.resource;

import com.github.dreamhead.moco.HttpProtocolVersion;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.resource.reader.*;
import com.github.dreamhead.moco.util.Cookies;
import com.github.dreamhead.moco.util.FileContentType;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

import java.io.File;

import static com.github.dreamhead.moco.model.MessageContent.content;
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
            public MessageContent readFor(final Optional<? extends Request> request) {
                return content().withContent(text).build();
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
            public MessageContent readFor(Optional<? extends Request> request) {
                return content().withContent(method.toUpperCase()).build();
            }
        });
    }

    public static Resource versionResource(final Resource version) {
        return resource(id("version"), DO_NOTHING_APPLIER, new ResourceReader() {
            @Override
            public MessageContent readFor(Optional<? extends Request> request) {
                String text = HttpProtocolVersion.versionOf(version.readFor(request).toString()).text();
                return content()
                        .withContent(text)
                        .build();
            }
        });
    }

    public static Resource cookieResource(final String key, final Resource resource) {
        return resource(id("cookie"), cookieConfigApplier(key, resource), new ResourceReader() {
            @Override
            public MessageContent readFor(Optional<? extends Request> request) {
                MessageContent messageContent = resource.readFor(request);
                return content()
                        .withContent(new Cookies().encodeCookie(key, messageContent.toString()))
                        .build();
            }
        });
    }

    public static ContentResource templateResource(final ContentResource template, ImmutableMap<String, ? extends Variable> variables) {
        return contentResource(id("template"), templateConfigApplier(template, variables), new TemplateResourceReader(template, variables));
    }

    public static Resource uriResource(final String uri) {
        return resource(id("uri"), uriConfigApplier("uri", uri), new ResourceReader() {
            @Override
            public MessageContent readFor(Optional<? extends Request> request) {
                return content().withContent(uri).build();
            }
        });
    }

    private static ContentResource contentResource(Identifiable id, ResourceConfigApplier applier, ContentResourceReader reader) {
        return new ContentResource(id, applier, reader);
    }

    private static Resource resource(Identifiable id, ResourceConfigApplier applier, ResourceReader reader) {
        return new Resource(id, applier, reader);
    }

    private ResourceFactory() {}
}
