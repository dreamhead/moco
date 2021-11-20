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
import com.github.dreamhead.moco.resource.reader.XmlResourceReader;
import com.github.dreamhead.moco.util.Cookies;
import com.github.dreamhead.moco.util.FileContentType;
import com.google.common.collect.ImmutableMap;
import com.google.common.net.MediaType;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.function.Function;

import static com.github.dreamhead.moco.model.MessageContent.content;
import static com.github.dreamhead.moco.resource.IdFactory.id;
import static com.github.dreamhead.moco.resource.ResourceConfigApplierFactory.DO_NOTHING_APPLIER;
import static com.github.dreamhead.moco.resource.ResourceConfigApplierFactory.cookieConfigApplier;
import static com.github.dreamhead.moco.resource.ResourceConfigApplierFactory.fileConfigApplier;
import static com.github.dreamhead.moco.resource.ResourceConfigApplierFactory.templateConfigApplier;
import static com.github.dreamhead.moco.resource.ResourceConfigApplierFactory.uriConfigApplier;
import static com.github.dreamhead.moco.util.Functions.checkApply;
import static com.google.common.net.MediaType.APPLICATION_BINARY;

public final class ResourceFactory {
    public static ContentResource textResource(final Function<Request, String> function) {
        return contentResource(id("text"), DO_NOTHING_APPLIER, new ContentResourceReader() {
            @Override
            public MediaType getContentType(final HttpRequest request) {
                return FileContentType.DEFAULT_CONTENT_TYPE_WITH_CHARSET;
            }

            @Override
            public MessageContent readFor(final Request request) {
                return content(checkApply(function, request));
            }
        });
    }

    public static ContentResource binaryResource(final Function<Request, Object> function) {
        return contentResource(id("binary"), DO_NOTHING_APPLIER, new ContentResourceReader() {
            @Override
            public MediaType getContentType(final HttpRequest request) {
                return APPLICATION_BINARY;
            }

            @Override
            public MessageContent readFor(final Request request) {
                Object result = checkApply(function, request);
                if (result instanceof byte[]) {
                    return content().withContent((byte[]) result).build();
                }

                if (result instanceof ByteBuffer) {
                    ByteBuffer buffer = (ByteBuffer) result;
                    return content().withContent(buffer).build();
                }

                if (result instanceof InputStream) {
                    InputStream is = (InputStream) result;
                    return content().withContent(is).build();
                }

                if (result instanceof String) {
                    final String text = (String) result;
                    return content().withContent(text).build();
                }

                throw new IllegalArgumentException("Not allowed " + result.getClass());
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

    public static ContentResource xmlResource(final Function<Request, Object> function) {
        return contentResource(id("xml"), DO_NOTHING_APPLIER,
                new XmlResourceReader(function));
    }

    public static ContentResource jsonResource(final Function<Request, Object> function) {
        return contentResource(id("json"), DO_NOTHING_APPLIER,
                new JsonResourceReader(function));
    }

    public static Resource methodResource(final String method) {
        return resource(id("method"), DO_NOTHING_APPLIER, request -> content(method.toUpperCase()));
    }

    public static Resource versionResource(final Resource version) {
        return resource(id("version"), DO_NOTHING_APPLIER, request -> {
            String text = HttpProtocolVersion.versionOf(version.readFor(request).toString()).text();
            return content(text);
        });
    }

    public static Resource versionResource(final HttpProtocolVersion version) {
        return resource(id("version"), DO_NOTHING_APPLIER, request -> content(version.text()));
    }

    public static Resource cookieResource(final String key, final Resource resource, final CookieAttribute... options) {
        return resource(id("cookie"), cookieConfigApplier(key, resource), request -> {
            MessageContent messageContent = resource.readFor(request);
            return content(new Cookies().encodeCookie(key, messageContent.toString(), options));
        });
    }

    public static ContentResource templateResource(final ContentResource template,
                                                   final ImmutableMap<String, ? extends Variable> variables) {
        return contentResource(id("template"), templateConfigApplier(template, variables),
                new TemplateResourceReader(template, variables));
    }

    public static Resource uriResource(final String uri) {
        return resource(id(MocoConfig.URI_ID), uriConfigApplier(MocoConfig.URI_ID, uri), request -> content(uri));
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
