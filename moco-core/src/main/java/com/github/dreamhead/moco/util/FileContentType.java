package com.github.dreamhead.moco.util;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;

import java.nio.charset.Charset;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;

public class FileContentType {
    public static final String DEFAULT_CONTENT_TYPE_WITH_CHARSET = "text/plain; charset=UTF-8";
    private static final String DEFAULT_CONTENT_TYPE = "text/plain";

    private static final ImmutableMap<String, String> contentTypeMap = ImmutableMap.<String, String>builder()
            .put("png", "image/png")
            .put("gif", "image/gif")
            .put("jpg", "image/jpeg")
            .put("jpeg", "image/jpeg")
            .put("tiff", "image/tiff")
            .put("css", "text/css")
            .put("html", "text/html")
            .put("txt", "text/plain")
            .put("js", "application/x-javascript")
            .put("json", "application/json")
            .put("pdf", "application/pdf")
            .put("zip", "application/zip")
            .put("xml", "text/xml")
            .build();

    private final String filename;
    private Optional<Charset> charset;

    public FileContentType(String filename, Optional<Charset> charset) {
        this.filename = filename;
        this.charset = charset;
    }

    public String getContentType() {
        String type = toContentType(Files.getFileExtension(filename));
        Optional<Charset> charset = toCharset(type);
        if (charset.isPresent()) {
            return type + "; charset=" + charset.get().displayName();
        }

        return type;
    }

    private Optional<Charset> toCharset(String type) {
        if (charset.isPresent()) {
            return charset;
        }

        if (DEFAULT_CONTENT_TYPE.equalsIgnoreCase(type)) {
            return of(Charsets.UTF_8);
        }

        return absent();
    }

    private String toContentType(String extension) {
        String contentType = contentTypeMap.get(extension.toLowerCase());
        return contentType != null ? contentType : DEFAULT_CONTENT_TYPE;
    }
}
