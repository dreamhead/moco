package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public final class AttachmentSetting extends BaseResourceSetting {
    private String filename;

    public String getFilename() {
        return filename;
    }

    @Override
    public String toString() {
        return this.toStringHelper()
                .omitNullValues()
                .add("filename", filename)
                .toString();
    }
}
