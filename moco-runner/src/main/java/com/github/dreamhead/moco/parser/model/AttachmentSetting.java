package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.google.common.base.MoreObjects;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class AttachmentSetting extends BaseResourceSetting {
    private String filename;

    public String getFilename() {
        return filename;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("filename", filename)
                .add("text", text)
                .add("file", file)
                .add("path resource", pathResource)
                .toString();
    }
}
