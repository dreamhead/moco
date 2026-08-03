package com.github.dreamhead.moco.util;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

import static java.util.Map.entry;
import static java.util.Optional.of;

public final class FileContentType {
    public static final MediaType DEFAULT_CONTENT_TYPE_WITH_CHARSET = MediaType.PLAIN_TEXT_UTF_8;

    private static final Map<String, MediaType> CONTENT_TYPES = Map.ofEntries(
            entry("png", MediaType.PNG),
            entry("gif", MediaType.GIF),
            entry("jpg", MediaType.JPEG),
            entry("jpeg", MediaType.JPEG),
            entry("tiff", MediaType.TIFF),
            entry("css", MediaType.create("text", "css")),
            entry("html", MediaType.create("text", "html")),
            entry("txt", MediaType.create("text", "plain")),
            entry("js", MediaType.create("application", "javascript")),
            entry("json", MediaType.create("application", "json")),
            entry("pdf", MediaType.PDF),
            entry("zip", MediaType.ZIP),
            entry("tar", MediaType.TAR),
            entry("gz", MediaType.GZIP),
            entry("xml", MediaType.create("text", "xml")));

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
        if (targetCharset.isPresent() && !type.charset().equals(targetCharset)) {
            return type.withCharset(targetCharset.get());
        }

        return type;
    }

    private Optional<Charset> toCharset(final MediaType type) {
        if (charset != null) {
            return of(charset);
        }

        if (type == null) {
            return of(StandardCharsets.UTF_8);
        }

        return type.charset();
    }

    private Optional<MediaType> toContentType(final String extension) {
        return Optional.ofNullable(CONTENT_TYPES.get(extension.toLowerCase()));
    }
}
