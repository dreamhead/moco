package com.github.dreamhead.moco.resource;

import com.github.dreamhead.moco.CookieAttribute;
import com.github.dreamhead.moco.HttpProtocolVersion;
import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.resource.reader.ClasspathFileResourceReader;
import com.github.dreamhead.moco.resource.reader.ContentResourceReader;
import com.github.dreamhead.moco.resource.reader.FileResourceReader;
import com.github.dreamhead.moco.resource.reader.JsonResourceReader;
import com.github.dreamhead.moco.resource.reader.TemplateResourceReader;
import com.github.dreamhead.moco.resource.reader.Variable;
import com.github.dreamhead.moco.util.Cookies;
import com.github.dreamhead.moco.util.FileContentType;
import com.google.common.collect.ImmutableMap;
import com.google.common.net.MediaType;

import java.nio.charset.Charset;

import static com.github.dreamhead.moco.model.MessageContent.content;
import static com.github.dreamhead.moco.resource.IdFactory.id;
import static com.github.dreamhead.moco.resource.ResourceConfigApplierFactory.DO_NOTHING_APPLIER;
import static com.github.dreamhead.moco.resource.ResourceConfigApplierFactory.cookieConfigApplier;
import static com.github.dreamhead.moco.resource.ResourceConfigApplierFactory.fileConfigApplier;
import static com.github.dreamhead.moco.resource.ResourceConfigApplierFactory.jsonConfigApplier;
import static com.github.dreamhead.moco.resource.ResourceConfigApplierFactory.templateConfigApplier;
import static com.github.dreamhead.moco.resource.ResourceConfigApplierFactory.uriConfigApplier;

public final class ResourceFactory {
    public static ContentResource textResource(final String text) {
        return contentResource(id("text"), DO_NOTHING_APPLIER, new ContentResourceReader() {
            @Override
            public MediaType getContentType(final HttpRequest request) {
                return FileContentType.DEFAULT_CONTENT_TYPE_WITH_CHARSET;
            }

            @Override
            public MessageContent readFor(final Request request) {
                return content(text);
            }
        });
    }

    public static ContentResource fileResource(final Resource filename, final Charset charset,
                                               final MocoConfig config) {
        return contentResource(id(MocoConfig.FILE_ID), fileConfigApplier(MocoConfig.FILE_ID, filename),
                new FileResourceReader(filename, charset, config));
    }

    public static ContentResource classpathFileResource(final Resource filename, final Charset charset) {
        return contentResource(id("pathresource"), DO_NOTHING_APPLIER,
                new ClasspathFileResourceReader(filename, charset));
    }

    public static ContentResource jsonResource(final Resource resource) {
        return contentResource(id("json"), jsonConfigApplier(resource), resource.reader(ContentResourceReader.class));
    }

    public static ContentResource jsonResource(final Object pojo) {
        return contentResource(id("json"), DO_NOTHING_APPLIER,
                new JsonResourceReader(pojo));
    }

    public static Resource methodResource(final String method) {
        return resource(id("method"), DO_NOTHING_APPLIER, new ResourceReader() {
            @Override
            public MessageContent readFor(final Request request) {
                return content(method.toUpperCase());
            }
        });
    }

    public static Resource versionResource(final Resource version) {
        return resource(id("version"), DO_NOTHING_APPLIER, new ResourceReader() {
            @Override
            public MessageContent readFor(final Request request) {
                String text = HttpProtocolVersion.versionOf(version.readFor(request).toString()).text();
                return content(text);
            }
        });
    }

    public static Resource versionResource(final HttpProtocolVersion version) {
        return resource(id("version"), DO_NOTHING_APPLIER, new ResourceReader() {
            @Override
            public MessageContent readFor(final Request request) {
                return content(version.text());
            }
        });
    }

    public static Resource cookieResource(final String key, final Resource resource, final CookieAttribute... options) {
        return resource(id("cookie"), cookieConfigApplier(key, resource), new ResourceReader() {
            @Override
            public MessageContent readFor(final Request request) {
                MessageContent messageContent = resource.readFor(request);
                return content(new Cookies().encodeCookie(key, messageContent.toString(), options));
            }
        });
    }

    public static ContentResource templateResource(final ContentResource template,
                                                   final ImmutableMap<String, ? extends Variable> variables) {
        return contentResource(id("template"), templateConfigApplier(template, variables),
                new TemplateResourceReader(template, variables));
    }

    public static Resource uriResource(final String uri) {
        return resource(id(MocoConfig.URI_ID), uriConfigApplier(MocoConfig.URI_ID, uri), new ResourceReader() {
            @Override
            public MessageContent readFor(final Request request) {
                return content(uri);
            }
        });
    }

    private static ContentResource contentResource(final Identifiable id, final ResourceConfigApplier applier,
                                                   final ContentResourceReader reader) {
        return new ContentResource(id, applier, reader);
    }

    private static Resource resource(final Identifiable id, final ResourceConfigApplier applier,
                                     final ResourceReader reader) {
        return new Resource(id, applier, reader);
    }

    private ResourceFactory() {
    }
}
