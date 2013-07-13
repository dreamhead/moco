package com.github.dreamhead.moco.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;

public class FileContentType {
    public static final String DEFAULT_CONTENT_TYPE = "text/plain; charset=UTF-8";

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

    public FileContentType(String filename) {
        this.filename = filename;
    }

    public String getContentType() {
        return toContentType(Files.getFileExtension(filename));
    }

    private String toContentType(String extension) {
        String contentType = contentTypeMap.get(extension.toLowerCase());
        return contentType != null ? contentType : DEFAULT_CONTENT_TYPE;
    }
}
