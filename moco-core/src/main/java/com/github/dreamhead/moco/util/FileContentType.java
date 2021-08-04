package com.github.dreamhead.moco.util;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import com.google.common.net.MediaType;

import java.nio.charset.Charset;
import java.util.Optional;

import static java.util.Optional.of;

public final class FileContentType {
    public static final MediaType DEFAULT_CONTENT_TYPE_WITH_CHARSET = MediaType.PLAIN_TEXT_UTF_8;

    private static final ImmutableMap<String, MediaType> CONTENT_TYPES = ImmutableMap.<String, MediaType>builder()
            .put("png", MediaType.PNG)
            .put("gif", MediaType.GIF)
            .put("jpg", MediaType.JPEG)
            .put("jpeg", MediaType.JPEG)
            .put("tiff", MediaType.TIFF)
            .put("css", MediaType.create("text", "css"))
            .put("html", MediaType.create("text", "html"))
            .put("txt", MediaType.create("text", "plain"))
            .put("js", MediaType.create("application", "javascript"))
            .put("json", MediaType.create("application", "json"))
            .put("pdf", MediaType.PDF)
            .put("zip", MediaType.ZIP)
            .put("tar", MediaType.TAR)
            .put("gz", MediaType.GZIP)
            .put("xml", MediaType.create("text", "xml"))
            .build();

    private final String filename;
    private final Charset charset;

    public FileContentType(final String filename) {
        this(filename, null);
    }

    public FileContentType(final String filename, final Charset charset) {
        this.filename = filename;
        this.charset = charset;
    }

    public MediaType getContentType() {
        Optional<MediaType> optionalType = toContentType(Files.getFileExtension(filename));
        Optional<Charset> targetCharset = toCharset(optionalType.orElse(null));

        MediaType type = optionalType.orElse(DEFAULT_CONTENT_TYPE_WITH_CHARSET);
        if (targetCharset.isPresent() && !type.charset().toJavaUtil().equals(targetCharset)) {
            return type.withCharset(targetCharset.get());
        }

        return type;
    }

    private Optional<Charset> toCharset(final MediaType type) {
        if (charset != null) {
            return of(charset);
        }

        if (type == null) {
            return of(Charsets.UTF_8);
        }

        return type.charset().toJavaUtil();
    }

    private Optional<MediaType> toContentType(final String extension) {
        return Optional.ofNullable(CONTENT_TYPES.get(extension.toLowerCase()));
    }
}
